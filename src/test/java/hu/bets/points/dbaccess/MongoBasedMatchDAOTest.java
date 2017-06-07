package hu.bets.points.dbaccess;

import com.github.fakemongo.Fongo;
import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import hu.bets.model.MatchResult;
import hu.bets.model.Result;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class MongoBasedMatchDAOTest {

    private MatchResult matchResult = new MatchResult("id1", new Result("1", "1", "1", 1, 1));

    @Mock
    private MongoCollection<Document> matchCollection;
    @Mock
    private MongoCollection<Document> errorCollection;

    private MongoBasedMatchDAO sut;

    @Before
    public void setp() {
        sut = new MongoBasedMatchDAO(matchCollection, errorCollection);
    }

    @Test
    public void shouldSaveMatchResultToTheDatabase() {
        sut.saveMatch(matchResult);

        String json = new Gson().toJson(matchResult);
        Mockito.verify(matchCollection).insertOne(Document.parse(json));
    }

    @Test
    public void shouldRetrieveNotProcessedMatches() {
        MongoDatabase database = new Fongo("fongo-server").getDatabase("test");
        MongoCollection<Document> collection = database.getCollection("testCollection");
        MongoCollection<Document> errorCollection = database.getCollection("errorCollection");

        sut = new FakeMongoBasedMatchDAO(collection, errorCollection);

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

    private MatchResult getRecord(LocalDateTime matchDate, String matchId) {
        return new MatchResult(matchId, new Result("1", "1", "1", 1, 1), matchDate);
    }

    class FakeMongoBasedMatchDAO extends MongoBasedMatchDAO {
        FakeMongoBasedMatchDAO(MongoCollection<Document> matchCollection, MongoCollection<Document> errorCollection) {
            super(matchCollection, errorCollection);
        }

        @Override
        protected LocalDateTime getCurrentTime() {
            return LocalDateTime.of(2017, 3, 19, 1, 10);
        }
    }
}