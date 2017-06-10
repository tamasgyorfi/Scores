package hu.bets.points.dbaccess;

import com.fiftyonred.mock_jedis.MockJedis;
import com.github.fakemongo.Fongo;
import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import hu.bets.model.FinalMatchResult;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static utils.TestUtils.getRecord;

@RunWith(MockitoJUnitRunner.class)
public class MongoBasedScoresServiceDAOTest {

    @Mock
    private MongoCollection<Document> matchCollection;

    private Jedis errorCollection = new MockJedis("test");
    private MongoBasedScoresServiceDAO sut;

    @Before
    public void setp() {
        sut = new MongoBasedScoresServiceDAO(matchCollection, null, errorCollection);
    }

    @Test
    public void shouldSaveMatchResultToTheDatabase() {
        FinalMatchResult finalMatchResult = getRecord(LocalDateTime.now(), "22");
        sut.saveMatch(finalMatchResult);

        String json = new Gson().toJson(finalMatchResult);
        Mockito.verify(matchCollection).insertOne(Document.parse(json));
    }

    @Test
    public void shouldRetrieveNotProcessedMatches() {
        MongoCollection<Document> collection = new Fongo("fongo-server").getDatabase("test").getCollection("testCollection");

        sut = new FakeMongoBasedScoresServiceDAO(collection, errorCollection);

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

    class FakeMongoBasedScoresServiceDAO extends MongoBasedScoresServiceDAO {
        FakeMongoBasedScoresServiceDAO(MongoCollection<Document> matchCollection, Jedis errorCollection) {
            super(matchCollection, null, errorCollection);
        }

        @Override
        public LocalDateTime getCurrentTime() {
            return LocalDateTime.of(2017, 3, 19, 1, 10);
        }
    }
}