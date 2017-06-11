package hu.bets.points.dbaccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jedis.lock.JedisLock;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import hu.bets.model.Bet;
import hu.bets.model.FinalMatchResult;
import hu.bets.model.Result;
import hu.bets.model.UnprocessedMatch;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Consumer;

public class MongoBasedScoresServiceDAO implements ScoresServiceDAO {

    private static final Logger LOGGER = Logger.getLogger(MongoBasedScoresServiceDAO.class);

    private static final int UNPROCESSED_MATCHES_COLLECTION = 0;
    private static final int MATCH_RESULTS_COLLECTION = 1;

    private static final int RECORD_AGE_THRESHOLD_HOURS = 24;
    private static final int LOCK_TIMEOUT = 1000;
    private static final int LOCK_EXPIRATION = 3000;
    private static final int EXPIRATION_TIME = 60 * 60;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private MongoCollection<Document> matchCollection;
    private MongoCollection<Document> scoreCollection;
    private Jedis cacheCollection;

    public MongoBasedScoresServiceDAO(MongoCollection<Document> matchCollection, MongoCollection<Document> scoreCollection, Jedis cacheCollection) {
        this.matchCollection = matchCollection;
        this.scoreCollection = scoreCollection;
        this.cacheCollection = cacheCollection;
    }

    @Override
    public void saveMatch(FinalMatchResult finalMatchResult) {
        String recordJson = toJson(finalMatchResult);
        matchCollection.insertOne(Document.parse(recordJson));
        cacheResult(finalMatchResult.getMatchId(), finalMatchResult.getResult());
    }

    @Override
    public void betProcessingFailedFor(String matchId) {
        cacheCollection.select(UNPROCESSED_MATCHES_COLLECTION);
        cacheCollection.set(matchId, matchId);
    }

    @Override
    public Collection<String> getFailedMatchIds() {
        Set<String> unprocessedMatches = getUnprocessedMatches();
        return filterUnprocessedMatches(unprocessedMatches);
    }

    @Override
    public void savePoints(Bet bet, int value) {
        Document toSave = new Document();
        toSave.put("userId", bet.getUserId());
        toSave.put("matchId", bet.getMatchId());
        toSave.put("competitionId", bet.getResult().getCompetitionId());
        toSave.put("betId", bet.getBetId());
        toSave.put("points", value);

        scoreCollection.insertOne(toSave);
    }

    @Override
    public Optional<Result> getResult(String matchId) {
        Optional<Result> cachedResult = findMatchInCache(matchId);
        if (!cachedResult.isPresent()) {
            Optional<Result> dbResult = findMatchInDatabase(matchId);
            if (dbResult.isPresent()) {
                cacheResult(matchId, dbResult.get());
                return dbResult;
            }
        }

        return cachedResult;
    }

    @Override
    public void saveNonProcessedMatches(Set<String> unprocessedMatches) {
        cacheCollection.select(UNPROCESSED_MATCHES_COLLECTION);

        for (String matchId : unprocessedMatches) {
            cacheCollection.set(matchId, matchId);
        }
    }

    private void cacheResult(String matchId, Result result) {
        cacheCollection.select(MATCH_RESULTS_COLLECTION);
        cacheCollection.setex(matchId, EXPIRATION_TIME, toJson(result));
    }

    private Optional<Result> findMatchInDatabase(String matchId) {
        Document matchResult = matchCollection.find(Filters.eq("matchId", matchId)).first();
        if (matchResult == null) {
            return Optional.empty();
        }

        return Optional.of(fromJson(matchResult.toJson(), FinalMatchResult.class).getResult());
    }

    private Optional<Result> findMatchInCache(String matchId) {
        cacheCollection.select(MATCH_RESULTS_COLLECTION);
        String json = cacheCollection.get(matchId);
        if (json == null) {
            return Optional.empty();
        }
        return Optional.of(fromJson(json, Result.class));
    }

    private Collection<String> filterUnprocessedMatches(Set<String> unprocessedMatches) {

        LocalDateTime thresholdDate = getCurrentTime().minusHours(RECORD_AGE_THRESHOLD_HOURS);

        Bson query = Filters.and(
                Filters.in("matchId", unprocessedMatches),
                Filters.gte("matchDate", DateUtil.format(thresholdDate))
        );

        FindIterable<Document> documents = matchCollection.find(query);
        List<String> result = new LinkedList<>();

        documents.forEach((Consumer<Document>) document -> result.add(fromJson(document.toJson(), UnprocessedMatch.class).getMatchId()));

        LOGGER.info("Unprocessed messages from the database: " + result);
        return result;
    }

    private <T> T fromJson(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            LOGGER.error("Unable to parse json: " + json, e);
        }

        return null;
    }

    private String toJson(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOGGER.error("Unable to convert to json; object was: " + object);
        }

        return "";
    }

    private Set<String> getUnprocessedMatches() {

        JedisLock lock = new JedisLock(cacheCollection, "processingLock", LOCK_TIMEOUT, LOCK_EXPIRATION);
        Set<String> resultRecords = new HashSet<>();

        try {
            lock.acquire();
            cacheCollection.select(UNPROCESSED_MATCHES_COLLECTION);
            try {
                resultRecords.addAll(cacheCollection.keys("*"));
                cacheCollection.flushDB();
            } catch (Exception e) {
                lock.release();
            }
        } catch (InterruptedException e) {
            // Nothing to do here.
        }

        LOGGER.info("Unprocessed match IDs from redis: " + resultRecords);
        return resultRecords;
    }

    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now(ZoneId.of("Europe/London"));
    }
}
