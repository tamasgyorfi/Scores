package hu.bets.points.dbaccess;

import com.fiftyonred.mock_jedis.MockJedis;
import com.github.fakemongo.Fongo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import hu.bets.model.Bet;
import hu.bets.model.FinalMatchResult;
import hu.bets.model.Result;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static utils.TestUtils.getRecord;

@RunWith(MockitoJUnitRunner.class)
public class MongoBasedScoresServiceDAOTest {

    static class MongoHolder {
        private static MongoDatabase fongo = new Fongo("fongo-server").getDatabase("test");
        private static MongoCollection<Document> matchCollection = fongo.getCollection("matchCollection");
        private static MongoCollection<Document> scoresCollection = fongo.getCollection("scoresCollection");

        static MongoCollection getMatchCollection() {
            return matchCollection;
        }

        static MongoCollection getScoresCollection() {
            return scoresCollection;
        }
    }

    private Jedis cacheCollection = new MockJedis("test");
    private MongoBasedScoresServiceDAO sut;

    @Before
    public void setp() {
        sut = new FakeMongoBasedScoresServiceDAO(MongoHolder.getMatchCollection(), MongoHolder.getScoresCollection(), cacheCollection);
    }

    @After
    public void tearDown() {
        cacheCollection.flushAll();
    }

    @Test
    public void shouldSaveMatchResultToTheDatabase() {
        MongoCollection matchCollection = Mockito.mock(MongoCollection.class);
        FinalMatchResult finalMatchResult = getRecord(LocalDateTime.now(), "22");
        sut = new MongoBasedScoresServiceDAO(matchCollection, null, cacheCollection);
        sut.saveMatch(finalMatchResult);

        String json = new Gson().toJson(finalMatchResult);
        verify(matchCollection).insertOne(Document.parse(json));
    }

    @Test
    public void shouldRetrieveNotProcessedMatches() {
        LocalDateTime out = sut.getCurrentTime().minusHours(48);
        LocalDateTime in = sut.getCurrentTime().minusHours(8);

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

        sut.betProcessingFailedFor("match1");
        sut.betProcessingFailedFor("match2");
        sut.betProcessingFailedFor("match9");
        sut.betProcessingFailedFor("match10");

        assertEquals(Arrays.asList("match1", "match10"), sut.getFailedMatchIds());
    }

    @Test
    public void shouldCorrectlySaveThePointsEntryIntoTheDatabase() {
        sut.savePoints(new Bet("user1", "match1", getResult(), "betId"), 10);

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
    }

    @Test
    public void shouldReturnFailedMatchIdsAfterMatchingCacheWithDb() {
        sut.saveMatch(new FinalMatchResult("id2", getResult(), sut.getCurrentTime()));
        sut.saveMatch(new FinalMatchResult("id3", getResult(), sut.getCurrentTime()));

        sut.betProcessingFailedFor("id1");
        sut.betProcessingFailedFor("id2");
        sut.betProcessingFailedFor("id3");

        assertEquals(Lists.newLinkedList(Arrays.asList("id2", "id3")), sut.getFailedMatchIds());
    }

    @Test
    public void shoulsSaveNonProcessedMatchIdsIntoCache() {
        sut.saveNonProcessedMatches(Sets.newHashSet("id1", "id2"));
        assertEquals("id1", cacheCollection.get("id1"));
        assertEquals("id2", cacheCollection.get("id2"));
    }

    @Test
    public void shouldGetMatchResultFromTheCacheWhenPresent() {
        MongoCollection matchCollection = Mockito.mock(MongoCollection.class);
        sut = new FakeMongoBasedScoresServiceDAO(matchCollection, MongoHolder.getScoresCollection(), cacheCollection);
        sut.saveMatch(new FinalMatchResult("id2", getResult(), sut.getCurrentTime()));

        assertEquals(Optional.of(getResult()), sut.getResult("id2"));

        verify(matchCollection).insertOne(any(Document.class));
        verify(matchCollection, never()).find(Bson.class);
    }

    @Test
    public void shouldGetMatchResultFromTheDbWhenCacheMiss() {
        Jedis cacheCollection = Mockito.mock(Jedis.class);
        sut = new FakeMongoBasedScoresServiceDAO(MongoHolder.getMatchCollection(), MongoHolder.getScoresCollection(), cacheCollection);
        sut.saveMatch(new FinalMatchResult("id2", getResult(), sut.getCurrentTime()));

        when(cacheCollection.get("id2")).thenReturn(null);
        assertEquals(Optional.of(getResult()), sut.getResult("id2"));
    }

    private Result getResult() {
        return new Result("comp1", "ht", "at", 1, 2);
    }

    class FakeMongoBasedScoresServiceDAO extends MongoBasedScoresServiceDAO {
        FakeMongoBasedScoresServiceDAO(MongoCollection matchCollection, MongoCollection scoresCollection, Jedis cacheCollection) {
            super(matchCollection, scoresCollection, cacheCollection);
        }

        @Override
        public LocalDateTime getCurrentTime() {
            return LocalDateTime.of(2017, 3, 19, 1, 10);
        }
    }
}