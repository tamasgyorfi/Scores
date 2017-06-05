package hu.bets.points.data;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import hu.bets.model.MatchResult;
import org.bson.Document;

public class MongoBasedMatchDAO implements MatchDAO {

    private MongoCollection<Document> matchCollection;

    public MongoBasedMatchDAO(MongoCollection<Document> matchCollection) {
        this.matchCollection = matchCollection;
    }

    @Override
    public void saveMatch(MatchResult matchResult) {
        MatchResultRecord matchResultRecord = new MatchResultRecord(matchResult, ProcessingState.NOT_STARTED);
        String recordJson = new Gson().toJson(matchResultRecord);
        matchCollection.insertOne(Document.parse(recordJson));
    }
}
