package hu.bets.points.dbaccess;

import com.github.jedis.lock.JedisLock;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import hu.bets.model.MatchResult;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Consumer;

public class MongoBasedMatchDAO implements MatchDAO {

    private static final Logger LOGGER = Logger.getLogger(MongoBasedMatchDAO.class);

    private static final int RECORD_AGE_THRESHOLD_HOURS = 24;
    private static final Gson GSON = new Gson();
    private static final int LOCK_TIMEOUT = 1000;
    private static final int LOCK_EXPIRATION = 3000;

    private MongoCollection<Document> matchCollection;
    private Jedis errorCollection;

    public MongoBasedMatchDAO(MongoCollection<Document> matchCollection, Jedis errorCollection) {
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
        errorCollection.set(matchId, matchId);
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

        LOGGER.info("Unprocessed messages from the database: " + result);
        return result;
    }

    private Set<String> getUnprocessedMatches() {

        JedisLock lock = new JedisLock(errorCollection, "processingLock", LOCK_TIMEOUT, LOCK_EXPIRATION);
        Set<String> resultRecords = new HashSet<>();

        try {
            lock.acquire();
            try {
                resultRecords.addAll(errorCollection.keys("*"));
                errorCollection.flushDB();
            } catch (Exception e) {
                lock.release();
            }
        } catch (InterruptedException e) {
            // Nothing to do here.
        }

        LOGGER.info("Unprocessed match IDs from redis: " + resultRecords);
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
