package hu.bets.points.dbaccess;

import com.fiftyonred.mock_jedis.MockJedis;
import com.github.fakemongo.Fongo;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import hu.bets.points.model.Bet;
import hu.bets.points.model.MatchResult;
import hu.bets.points.model.Result;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static utils.TestUtils.getRecord;

@RunWith(MockitoJUnitRunner.class)
public class DefaultScoresServiceDAOTest {

    static class MongoHolder {
        private static MongoDatabase fongo = new Fongo("fongo-server").getDatabase("test");
        private static MongoCollection<Document> matchCollection = fongo.getCollection("matchCollection");
        private static MongoCollection<Document> scoresCollection = fongo.getCollection("scoresCollection");
        private static MongoCollection<Document> toplistCollection = fongo.getCollection("toplistCollection");

        static MongoCollection getMatchCollection() {
            return matchCollection;
        }

        static MongoCollection getScoresCollection() {
            return scoresCollection;
        }

        static MongoCollection getToplistCollection() {
            return toplistCollection;
        }
    }

    @Mock
    private JedisPool jedisPool;
    private Jedis cacheCollection = new MockJedis("test");

    private DefaultScoresServiceDAO sut;

    @Before
    public void setup() {
        when(jedisPool.getResource()).thenReturn(cacheCollection);
        sut = new FakeDefaultScoresServiceDAO(MongoHolder.getMatchCollection(), MongoHolder.getScoresCollection(), MongoHolder.getToplistCollection(), jedisPool);
    }

    @After
    public void tearDown() {
        cacheCollection.flushAll();
        MongoHolder.getScoresCollection().drop();
        MongoHolder.getMatchCollection().drop();
        MongoHolder.getToplistCollection().drop();
    }

    @Test
    public void shouldSaveMatchResultToTheDatabase() {
        MongoCollection matchCollection = Mockito.mock(MongoCollection.class);
        MatchResult matchResult = getRecord(LocalDateTime.now(), "22");
        sut = new DefaultScoresServiceDAO(matchCollection, null, null, jedisPool);
        sut.saveMatch(matchResult);

        String json = new Gson().toJson(matchResult);
        verify(matchCollection).insertOne(Document.parse(json));
    }

    @Test
    public void shouldRetrieveNotProcessedMatches() {
        LocalDateTime out = sut.getCurrentTime().minusHours(48);
        LocalDateTime in = sut.getCurrentTime();

        sut.saveMatch(getRecord(in, "match1"));
        sut.saveMatch(getRecord(out, "match2"));
        sut.saveMatch(getRecord(in, "match3"));
        sut.saveMatch(getRecord(in, "match4"));
        sut.saveMatch(getRecord(out, "match5"));
        sut.saveMatch(getRecord(in, "match6"));
        sut.saveMatch(getRecord(in, "match7"));
        sut.saveMatch(getRecord(out, "match8"));
        sut.saveMatch(getRecord(out, "match9"));
        sut.saveMatch(getRecord(in, "match10"));

        sut.saveNonProcessedMatches(Sets.newHashSet("match1", "match2", "match9", "match10"));

        assertEquals(Sets.newHashSet("match1", "match10"), sut.getFailedMatchIds());
    }

    @Test
    public void shouldCorrectlySaveThePointsEntryIntoTheDatabaseUserNotInToplistCollection() {
        sut.savePoints(new Bet("user1", "match1", getResult("match1", sut.getCurrentTime().toString()), "betId"), 10);

        FindIterable<Document> documents = MongoHolder.getScoresCollection().find(Filters.eq("betId", "betId"));
        Document document = documents.first();

        assertTrue(document.containsKey("userId"));
        assertEquals(document.get("userId"), "user1");

        assertTrue(document.containsKey("matchId"));
        assertEquals(document.get("matchId"), "match1");

        assertTrue(document.containsKey("competitionId"));
        assertEquals(document.get("competitionId"), "comp1");

        assertTrue(document.containsKey("points"));
        assertEquals(document.get("points"), 10);

        FindIterable<Document> toplist = MongoHolder.getToplistCollection().find(Filters.eq("userId", "user1"));
        Document doc = toplist.first();

        assertTrue(doc.containsKey("userId"));
        assertTrue(doc.containsKey("points"));

        assertEquals(doc.get("points"), 10);
    }

    @Test
    public void shouldCorrectlySaveThePointsEntryIntoTheDatabaseUserInToplistCollection() {
        MongoHolder.getToplistCollection().insertOne(new Document("userId", "user1").append("points", 13));
        sut.savePoints(new Bet("user1", "match1", getResult("match1", sut.getCurrentTime().toString()), "betId"), 10);

        FindIterable<Document> documents = MongoHolder.getScoresCollection().find(Filters.eq("betId", "betId"));
        Document document = documents.first();

        assertTrue(document.containsKey("userId"));
        assertEquals(document.get("userId"), "user1");

        assertTrue(document.containsKey("matchId"));
        assertEquals(document.get("matchId"), "match1");

        assertTrue(document.containsKey("competitionId"));
        assertEquals(document.get("competitionId"), "comp1");

        assertTrue(document.containsKey("points"));
        assertEquals(document.get("points"), 10);

        FindIterable<Document> toplist = MongoHolder.getToplistCollection().find(Filters.eq("userId", "user1"));
        Document doc = toplist.first();

        assertTrue(doc.containsKey("userId"));
        assertTrue(doc.containsKey("points"));

        assertEquals(doc.get("points"), 23);
    }

    @Test
    public void shouldReturnFailedMatchIdsAfterMatchingCacheWithDb() {
        sut.saveMatch(new MatchResult(getResult("id2", sut.getCurrentTime().toString()), sut.getCurrentTime()));
        sut.saveMatch(new MatchResult(getResult("id3", sut.getCurrentTime().toString()), sut.getCurrentTime()));

        sut.saveNonProcessedMatches(Sets.newHashSet("id1", "id2", "id3"));

        assertEquals(Sets.newHashSet("id2", "id3"), sut.getFailedMatchIds());
    }

    @Test
    public void shouldSaveNonProcessedMatchIdsIntoCache() {
        sut.saveNonProcessedMatches(Sets.newHashSet("id1", "id2"));
        assertEquals("id1", cacheCollection.get("id1"));
        assertEquals("id2", cacheCollection.get("id2"));
    }

    @Test
    public void shouldGetMatchResultFromTheCacheWhenPresent() throws InterruptedException {
        MongoCollection matchCollection = Mockito.mock(MongoCollection.class);
        sut = new FakeDefaultScoresServiceDAO(matchCollection, MongoHolder.getScoresCollection(), MongoHolder.getToplistCollection(), jedisPool);
        sut.saveMatch(new MatchResult(getResult("id2",sut.getCurrentTime().toString()), sut.getCurrentTime()));

        Optional<Result> result = sut.getResult("id2");
        assertEquals(Optional.ofNullable(getResult("id2", sut.getCurrentTime().toString())), result);

        verify(matchCollection).insertOne(any(Document.class));
        verify(matchCollection, never()).find(Bson.class);
    }

    @Test
    public void shouldGetMatchResultFromTheDbWhenCacheMiss() {
        Jedis cacheCollection = Mockito.mock(Jedis.class);
        sut = new FakeDefaultScoresServiceDAO(MongoHolder.getMatchCollection(), MongoHolder.getScoresCollection(), MongoHolder.getToplistCollection(), jedisPool);
        sut.saveMatch(new MatchResult(getResult("id2", sut.getCurrentTime().toString()), sut.getCurrentTime()));

        when(cacheCollection.get("id2")).thenReturn(null);
        assertEquals(Optional.of(getResult("id2", sut.getCurrentTime().toString())), sut.getResult("id2"));
    }

    private Result getResult(String matchId, String date) {
        return new Result(matchId, "comp1", "ht", "at", 1, 2, date);
    }

    class FakeDefaultScoresServiceDAO extends DefaultScoresServiceDAO {
        FakeDefaultScoresServiceDAO(MongoCollection matchCollection, MongoCollection scoresCollection, MongoCollection toplistCollection, JedisPool jedisPool) {
            super(matchCollection, scoresCollection, toplistCollection, jedisPool);
            matchExpiration = 1;
        }

        @Override
        public LocalDateTime getCurrentTime() {
            return LocalDateTime.of(2017, 3, 19, 1, 10);
        }

    }
}