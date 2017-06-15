package integration;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import hu.bets.common.util.EnvironmentVarResolver;
import hu.bets.common.util.hash.MD5HashGenerator;
import hu.bets.config.ApplicationConfig;
import hu.bets.config.MessagingConfig;
import hu.bets.config.WebConfig;
import hu.bets.messaging.MessagingConstants;
import hu.bets.model.Bet;
import hu.bets.model.BetBatch;
import hu.bets.model.MatchResult;
import hu.bets.model.Result;
import hu.bets.points.dbaccess.MongoBasedScoresServiceDAO;
import hu.bets.steps.Given;
import hu.bets.steps.When;
import hu.bets.steps.util.ApplicationContextHolder;
import hu.bets.utils.JsonUtils;
import hu.bets.web.model.ResultResponse;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.bson.Document;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import utils.TestUtils;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static utils.TestUtils.CORRECT_MATCH_END_PAYLOAD;
import static utils.TestUtils.getRecord;

public class SaveMatchIntegrationTest {

    @BeforeClass
    public static void setup() throws Exception {
        Given.environmentIsUpAndRunning(ApplicationConfig.class,
                FakeDatabaseConfig.class,
                MessagingConfig.class,
                WebConfig.class);

        TimeUnit.SECONDS.sleep(2);
    }

    @After
    public void after() {
        ApplicationContextHolder.getBean(Jedis.class).flushAll();
        FakeDatabaseConfig.FongoResultsCollectionHolder.getMatchResultCollection().drop();
        FakeDatabaseConfig.FongoResultsCollectionHolder.getScoresCollection().drop();
    }

    private static final int DB_INDEX = 1;
    private static final long WAIT_TIME_SECONDS = 3;

    private Bet bet1 = new Bet("user1", "match100", new Result("match100", "compId100", "team1", "team2", 3, 1), "bet100");
    private Bet bet2 = new Bet("user2", "match200", new Result("match200", "compId100", "team3", "team9", 3, 3), "bet200");
    private Bet bet3 = new Bet("user3", "match300", new Result("match300", "compId100", "team3", "team9", 3, 3), "bet300");

    private List<Bet> bets = Lists.newArrayList(bet1, bet2, bet3);


    @Test
    public void resultShouldBeRejectedIfSchemaValidationFails() throws Exception {

        String endpoint = "http://" + EnvironmentVarResolver.getEnvVar("HOST") +
                ":" + EnvironmentVarResolver.getEnvVar("PORT") + "/scores/football/v1/results/AA-777-vxF";

        HttpResponse httpResponse = When.iMakeAPostRequest(endpoint, "{\"payload\":\"none\"}");
        ResultResponse resultResponse = new Gson().fromJson(EntityUtils.toString(httpResponse.getEntity()), ResultResponse.class);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, resultResponse.getResponseCode());
        assertTrue(resultResponse.getError().contains("org.everit.json.schema.ValidationException"));
        assertEquals("", resultResponse.getResponsePayload());
    }

    @Test
    public void resultShouldBeSavedIfSchemaValidationPasses() throws Exception {

        String endpoint = "http://" + EnvironmentVarResolver.getEnvVar("HOST") +
                ":" + EnvironmentVarResolver.getEnvVar("PORT") + "/scores/football/v1/results/match100";

        HttpResponse httpResponse = When.iMakeAPostRequest(endpoint, CORRECT_MATCH_END_PAYLOAD);
        ResultResponse resultResponse = new Gson().fromJson(EntityUtils.toString(httpResponse.getEntity()), ResultResponse.class);

        assertEquals(Response.Status.ACCEPTED, resultResponse.getResponseCode());
        assertEquals("", resultResponse.getError());
        assertEquals("Match results saved.", resultResponse.getResponsePayload());
    }

    @Test
    public void resultShouldErrorIfMatchIdDoesNotMatchPayloadMatchId() throws Exception {

        String endpoint = "http://" + EnvironmentVarResolver.getEnvVar("HOST") +
                ":" + EnvironmentVarResolver.getEnvVar("PORT") + "/scores/football/v1/results/Unknown";

        HttpResponse httpResponse = When.iMakeAPostRequest(endpoint, CORRECT_MATCH_END_PAYLOAD);
        ResultResponse resultResponse = new Gson().fromJson(EntityUtils.toString(httpResponse.getEntity()), ResultResponse.class);

        assertEquals(Response.Status.BAD_REQUEST, resultResponse.getResponseCode());
        assertEquals("", resultResponse.getResponsePayload());
        assertEquals("MatchId mismatch.", resultResponse.getError());
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
    public void testRedisMongoInterpay() {
        MongoBasedScoresServiceDAO matchDAO = ApplicationContextHolder.getBean(MongoBasedScoresServiceDAO.class);

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

        matchDAO.betProcessingFailedFor("match1");
        matchDAO.betProcessingFailedFor("match2");
        matchDAO.betProcessingFailedFor("match9");
        matchDAO.betProcessingFailedFor("match10");

        assertEquals(Arrays.asList("match1", "match10"), matchDAO.getFailedMatchIds());
    }

    @Test
    public void testIncomingBetBatchHandling() throws Exception {
        Jedis cache = ApplicationContextHolder.getBean(Jedis.class);

        Channel senderChannel = ApplicationContextHolder.getBean(Channel.class);
        Channel receiverChannel = ApplicationContextHolder.getBean(Channel.class);
        TestCosumer testConsumer = new TestCosumer(receiverChannel);

        receiverChannel.queueBind(MessagingConstants.SCORES_TO_BETS_QUEUE, MessagingConstants.EXCHANGE_NAME, MessagingConstants.SCORES_TO_BETS_ROUTE);
        receiverChannel.basicConsume(MessagingConstants.SCORES_TO_BETS_QUEUE, true, testConsumer);

        MongoCollection matchCollection = FakeDatabaseConfig.FongoResultsCollectionHolder.getMatchResultCollection();

        MatchResult result1 = new MatchResult("match100", new Result("match100", "compId100", "team1", "team2", 1, 1));
        MatchResult result2 = new MatchResult("match200", new Result("match200", "compId100", "team3", "team9", 3, 3));

        BetBatch betBatch = new BetBatch(3, bets, new MD5HashGenerator().getHash(bets));

        cache.select(DB_INDEX);
        cache.set("match100", new JsonUtils().toJson(result1.getResult()));
        matchCollection.insertOne(Document.parse(new JsonUtils().toJson(result2)));

        senderChannel.basicPublish(MessagingConstants.EXCHANGE_NAME, MessagingConstants.BETS_TO_SCORES_ROUTE, null, new JsonUtils().toJson(betBatch).getBytes());

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
        TestCosumer testConsumer = new TestCosumer(receiverChannel);

        receiverChannel.queueBind(MessagingConstants.SCORES_TO_BETS_QUEUE, MessagingConstants.EXCHANGE_NAME, MessagingConstants.SCORES_TO_BETS_ROUTE);
        receiverChannel.basicConsume(MessagingConstants.SCORES_TO_BETS_QUEUE, true, testConsumer);


        String endpoint = "http://" + EnvironmentVarResolver.getEnvVar("HOST") +
                ":" + EnvironmentVarResolver.getEnvVar("PORT") + "/scores/football/v1/results/" + uniqueId;

        HttpResponse httpResponse = When.iMakeAPostRequest(endpoint, TestUtils.getMatchEndPayload(uniqueId));
        ResultResponse resultResponse = new Gson().fromJson(EntityUtils.toString(httpResponse.getEntity()), ResultResponse.class);

        assertEquals(Response.Status.ACCEPTED, resultResponse.getResponseCode());
        assertEquals("", resultResponse.getError());
        assertEquals("Match results saved.", resultResponse.getResponsePayload());

        TimeUnit.SECONDS.sleep(WAIT_TIME_SECONDS);

        assertNotNull(FakeDatabaseConfig.FongoResultsCollectionHolder
                .getMatchResultCollection()
                .find(Filters.eq("result.matchId", uniqueId))
                .first());

        assertNotNull(ApplicationContextHolder.getBean(Jedis.class).get(uniqueId));

        assertEquals("{\"payload\":[\"" + uniqueId + "\"],\"type\":\"BETS_REQUEST\"}", testConsumer.getMessage());

        senderChannel.close();
        receiverChannel.close();
    }

    private static class TestCosumer extends DefaultConsumer {

        private String message;

        public TestCosumer(Channel channel) {
            super(channel);
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope,
                                   AMQP.BasicProperties properties, byte[] body) throws IOException {
            message = new String(body, "UTF-8");
        }

        public String getMessage() {
            return message;
        }
    }

}
