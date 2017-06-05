package hu.bets.points.data;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import hu.bets.model.MatchResult;
import hu.bets.model.Result;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MongoBasedMatchDAOTest {

    private MatchResult matchResult = new MatchResult("id1", new Result("1", "1", "1", 1, 1));

    @Mock
    private MongoCollection<Document> matchCollection;

    private MongoBasedMatchDAO sut;

    @Before
    public void setp() {
        sut = new MongoBasedMatchDAO(matchCollection);
    }

    @Test
    public void shouldSaveMatchResultToTheDatabase() {
        sut.saveMatch(matchResult);

        String json = new Gson().toJson(new MatchResultRecord(matchResult, ProcessingState.NOT_STARTED));
        Mockito.verify(matchCollection).insertOne(Document.parse(json));
    }
}