package hu.bets.points.dbaccess;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import hu.bets.model.MatchResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Consumer;

public class MongoBasedMatchDAO implements MatchDAO {

    private static final int RECORD_AGE_THRESHOLD_HOURS = 24;
    private static final Gson GSON = new Gson();

    private MongoCollection<Document> matchCollection;
    private MongoCollection<Document> errorCollection;

    public MongoBasedMatchDAO(MongoCollection<Document> matchCollection, MongoCollection<Document> errorCollection) {
        this.matchCollection = matchCollection;
        this.errorCollection = errorCollection;
    }

    @Override
    public void saveMatch(MatchResult matchResult) {
        String recordJson = GSON.toJson(matchResult);
        matchCollection.insertOne(Document.parse(recordJson));
    }

    @Override
    public void betProcessingFailedFor(String matchId) {
        errorCollection.insertOne(Document.parse(GSON.toJson(new UnprocessedMatch(matchId))));
    }

    @Override
    public Collection<String> getFailedMatchIds() {
        Set<String> unprocessedMatches = getUnprocessedMatches();
        return filterUnprocessedMatches(unprocessedMatches);
    }

    private Collection<String> filterUnprocessedMatches(Set<String> unprocessedMatches) {

        LocalDateTime thresholdDate = getCurrentTime().minusHours(RECORD_AGE_THRESHOLD_HOURS);

        Bson query = Filters.and(
                Filters.in("matchId", unprocessedMatches),
                Filters.gte("matchDate", DateUtil.format(thresholdDate))
        );

        FindIterable<Document> documents = matchCollection.find(query);
        List<String> result = new LinkedList<>();

        documents.forEach((Consumer<Document>) document -> result.add(GSON.fromJson(document.toJson(), UnprocessedMatch.class).getMatchId()));

        return result;
    }

    private Set<String> getUnprocessedMatches() {

        FindIterable<Document> documents = errorCollection.find();

        Set<String> resultRecords = new HashSet<>();
        documents.forEach((Consumer<Document>) document -> resultRecords.add(GSON.fromJson(document.toJson(), UnprocessedMatch.class).getMatchId()));
        errorCollection.drop();

        return resultRecords;
    }

    protected LocalDateTime getCurrentTime() {
        return LocalDateTime.now(ZoneId.of("Europe/London"));
    }

    private static class UnprocessedMatch {
        private String matchId;

        public UnprocessedMatch(String matchId) {
            this.matchId = matchId;
        }

        public String getMatchId() {
            return matchId;
        }
    }
}
