package integration;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.rabbitmq.client.Channel;
import hu.bets.common.util.EnvironmentVarResolver;
import hu.bets.common.util.hash.MD5HashGenerator;
import hu.bets.common.util.json.Json;
import hu.bets.points.config.ApplicationConfig;
import hu.bets.points.config.MessagingConfig;
import hu.bets.points.config.WebConfig;
import hu.bets.points.dbaccess.DefaultScoresServiceDAO;
import hu.bets.points.messaging.MessagingConstants;
import hu.bets.points.model.*;
import hu.bets.points.web.model.ResultResponse;
import hu.bets.points.web.model.ToplistResponsePayload;
import hu.bets.steps.Given;
import hu.bets.steps.When;
import hu.bets.steps.util.ApplicationContextHolder;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import utils.TestConsumer;
import utils.TestUtils;

import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static utils.TestUtils.CORRECT_MATCH_END_PAYLOAD;
import static utils.TestUtils.getRecord;

public class IntegrationTest {

    @BeforeClass
    public static void setup() throws Exception {
        Given.environmentIsUpAndRunning(ApplicationConfig.class,
                FakeDatabaseConfig.class,
                MessagingConfig.class,
                WebConfig.class);

        TimeUnit.SECONDS.sleep(2);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        Given.environmentIsShutDown();
    }

    @After
    public void after() {
        ApplicationContextHolder.getBean(JedisPool.class).getResource().flushAll();
        FakeDatabaseConfig.FongoResultsCollectionHolder.getMatchResultCollection().drop();
        FakeDatabaseConfig.FongoResultsCollectionHolder.getScoresCollection().drop();
        FakeDatabaseConfig.FongoResultsCollectionHolder.getScoresCollection().drop();
    }

    private static final int MATCH_COLLECTION_INDEX = 1;
    private static final long WAIT_TIME_SECONDS = 3;

    private Bet bet1 = new Bet("user1", "match100", new Result("match100", "compId100", "team1", "team2", 3, 1), "bet100");
    private Bet bet2 = new Bet("user2", "match200", new Result("match200", "compId100", "team3", "team9", 3, 3), "bet200");
    private Bet bet3 = new Bet("user3", "match300", new Result("match300", "compId100", "team3", "team9", 3, 3), "bet300");

    private List<Bet> bets = Lists.newArrayList(bet1, bet2, bet3);


    @Test
    public void resultShouldBeRejectedIfSchemaValidationFails() throws Exception {

        String endpoint = "http://" + EnvironmentVarResolver.getEnvVar("HOST") +
                ":" + EnvironmentVarResolver.getEnvVar("PORT") + "/scores/football/v1/results";

        HttpResponse httpResponse = When.iMakeAPostRequest(endpoint, "{\"payload\":\"none\"}");
        ResultResponse resultResponse = new Gson().fromJson(EntityUtils.toString(httpResponse.getEntity()), ResultResponse.class);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), httpResponse.getStatusLine().getStatusCode());
        assertTrue(resultResponse.getError().contains("#: 2 schema violations found\n" +
                "#: required key [token] not found\n" +
                "#: required key [results] not found"));
        assertEquals("", resultResponse.getResponsePayload());
    }

    @Test
    public void resultShouldBeSavedIfSchemaValidationPasses() throws Exception {

        String endpoint = "http://" + EnvironmentVarResolver.getEnvVar("HOST") +
                ":" + EnvironmentVarResolver.getEnvVar("PORT") + "/scores/football/v1/results";

        HttpResponse httpResponse = When.iMakeAPostRequest(endpoint, CORRECT_MATCH_END_PAYLOAD);
        ResultResponse resultResponse = new Gson().fromJson(EntityUtils.toString(httpResponse.getEntity()), ResultResponse.class);

        assertEquals(Response.Status.ACCEPTED.getStatusCode(), httpResponse.getStatusLine().getStatusCode());
        assertEquals("", resultResponse.getError());
        assertEquals("Match results saved.", resultResponse.getResponsePayload());
    }

    @Test
    public void infoEndpointShouldReplyWIthOk() throws Exception {

        String endpoint = "http://" + EnvironmentVarResolver.getEnvVar("HOST") +
                ":" + EnvironmentVarResolver.getEnvVar("PORT") + "/scores/football/v1/info";

        HttpResponse httpResponse = When.iMakeAGetRequest(endpoint);
        String response = EntityUtils.toString(httpResponse.getEntity());

        assertEquals("<html><h1>Football-Scores up and running</h1></html>", response);
    }

    @Test
    public void testRedisMongoInterplay() {
        DefaultScoresServiceDAO matchDAO = ApplicationContextHolder.getBean(DefaultScoresServiceDAO.class);

        LocalDateTime out = matchDAO.getCurrentTime().minusHours(48);
        LocalDateTime in = matchDAO.getCurrentTime().minusHours(8);

        matchDAO.saveMatch(getRecord(in, "match1"));
        matchDAO.saveMatch(getRecord(out, "match2"));
        matchDAO.saveMatch(getRecord(in, "match3"));
        matchDAO.saveMatch(getRecord(in, "match4"));
        matchDAO.saveMatch(getRecord(out, "match5"));
        matchDAO.saveMatch(getRecord(in, "match6"));
        matchDAO.saveMatch(getRecord(in, "match7"));
        matchDAO.saveMatch(getRecord(out, "match8"));
        matchDAO.saveMatch(getRecord(out, "match9"));
        matchDAO.saveMatch(getRecord(in, "match10"));

        matchDAO.saveNonProcessedMatches(Sets.newHashSet("match1", "match2", "match9", "match10"));

        assertEquals(Sets.newHashSet("match1", "match10"), matchDAO.getFailedMatchIds());
    }

    @Test
    public void testIncomingBetBatchHandling() throws Exception {
        Jedis jedis = ApplicationContextHolder.getBean(JedisPool.class).getResource();

        Channel senderChannel = ApplicationContextHolder.getBean(Channel.class);
        Channel receiverChannel = ApplicationContextHolder.getBean(Channel.class);
        TestConsumer testConsumer = new TestConsumer(receiverChannel);

        receiverChannel.queueBind(MessagingConstants.SCORES_TO_BETS_QUEUE, MessagingConstants.EXCHANGE_NAME, MessagingConstants.SCORES_TO_BETS_ROUTE);
        receiverChannel.basicConsume(MessagingConstants.SCORES_TO_BETS_QUEUE, true, testConsumer);

        MongoCollection matchCollection = FakeDatabaseConfig.FongoResultsCollectionHolder.getMatchResultCollection();

        MatchResult result1 = new MatchResult("match100", new Result("match100", "compId100", "team1", "team2", 1, 1));
        MatchResult result2 = new MatchResult("match200", new Result("match200", "compId100", "team3", "team9", 3, 3));

        BetBatch betBatch = new BetBatch(3, bets, new MD5HashGenerator().getHash(bets));

        jedis.select(MATCH_COLLECTION_INDEX);
        jedis.set("match100", new Json().toJson(result1.getResult()));
        matchCollection.insertOne(Document.parse(new Json().toJson(result2)));

        senderChannel.basicPublish(MessagingConstants.EXCHANGE_NAME, MessagingConstants.BETS_TO_SCORES_ROUTE, null, new Json().toJson(betBatch).getBytes());
        receiverChannel.basicConsume(MessagingConstants.SCORES_TO_BETS_QUEUE, true, testConsumer);
        TimeUnit.SECONDS.sleep(WAIT_TIME_SECONDS);

        assertEquals("{\"payload\":[\"bet200\",\"bet100\"],\"type\":\"ACKNOWLEDGE_REQUEST\"}", testConsumer.getMessage());

        senderChannel.close();
        receiverChannel.close();
    }

    @Test
    public void correctIncomingPayloadShouldBeSavedInTheDatabaseAndBetsRequested() throws Exception {
        String uniqueId = "UniqueID";

        Channel senderChannel = ApplicationContextHolder.getBean(Channel.class);
        Channel receiverChannel = ApplicationContextHolder.getBean(Channel.class);
        TestConsumer testConsumer = new TestConsumer(receiverChannel);

        receiverChannel.queueBind(MessagingConstants.SCORES_TO_BETS_QUEUE, MessagingConstants.EXCHANGE_NAME, MessagingConstants.SCORES_TO_BETS_ROUTE);
        receiverChannel.basicConsume(MessagingConstants.SCORES_TO_BETS_QUEUE, true, testConsumer);

        String endpoint = "http://" + EnvironmentVarResolver.getEnvVar("HOST") +
                ":" + EnvironmentVarResolver.getEnvVar("PORT") + "/scores/football/v1/results";

        HttpResponse httpResponse = When.iMakeAPostRequest(endpoint, TestUtils.getMatchEndPayload(uniqueId));
        ResultResponse resultResponse = new Gson().fromJson(EntityUtils.toString(httpResponse.getEntity()), ResultResponse.class);

        assertEquals(Response.Status.ACCEPTED.getStatusCode(), httpResponse.getStatusLine().getStatusCode());
        assertEquals("", resultResponse.getError());
        assertEquals("Match results saved.", resultResponse.getResponsePayload());

        TimeUnit.SECONDS.sleep(WAIT_TIME_SECONDS);

        assertNotNull(FakeDatabaseConfig.FongoResultsCollectionHolder
                .getMatchResultCollection()
                .find(Filters.eq("result.matchId", uniqueId))
                .first());

        Jedis jedis = ApplicationContextHolder.getBean(JedisPool.class).getResource();
        jedis.select(MATCH_COLLECTION_INDEX);
        assertNotNull(jedis.get(uniqueId));

        assertEquals("{\"payload\":[\"" + uniqueId + "\"],\"type\":\"BETS_REQUEST\"}", testConsumer.getMessage());

        senderChannel.close();
        receiverChannel.close();
    }

    @Test
    public void shouldSaveABetIdOnlyOnce() {
        DefaultScoresServiceDAO matchDAO = ApplicationContextHolder.getBean(DefaultScoresServiceDAO.class);

        Bet bet = new Bet("user1", "match1", new Result("match1", "comp1", "h", "a", 1, 1), "bet1");
        matchDAO.savePoints(bet, 10);
        matchDAO.savePoints(bet, 8);

        long count = FakeDatabaseConfig.FongoResultsCollectionHolder.getScoresCollection().count(Filters.eq("betId", "bet1"));
        assertEquals(1, count);
    }

    @Test
    public void shouldReturnPointsForUsers() throws Exception {

        MongoCollection toplistCollection = FakeDatabaseConfig.FongoResultsCollectionHolder.getToplistCollection();


        toplistCollection.insertOne(Document.parse("{\"userId\": \"user11\", \"points\": 10}"));
        toplistCollection.insertOne(Document.parse("{\"userId\": \"user33\", \"points\": 17}"));

        String endpoint = "http://" + EnvironmentVarResolver.getEnvVar("HOST") +
                ":" + EnvironmentVarResolver.getEnvVar("PORT") + "/scores/football/v1/toplist";

        HttpResponse httpResponse = When.iMakeAPostRequest(endpoint, TestUtils.getToplistPayload(Arrays.asList("user11", "user22", "user33")));
        ToplistResponsePayload resultResponse = new Gson().fromJson(EntityUtils.toString(httpResponse.getEntity()), ToplistResponsePayload.class);

        List<ToplistEntry> entries = new ArrayList<>();
        entries.add(new ToplistEntry("user11", 10));
        entries.add(new ToplistEntry("user22", 0));
        entries.add(new ToplistEntry("user33", 17));

        assertEquals(new ToplistResponsePayload(entries, ""), resultResponse);
    }
}
