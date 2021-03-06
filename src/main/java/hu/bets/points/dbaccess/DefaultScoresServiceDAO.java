package hu.bets.points.dbaccess;

import com.github.jedis.lock.JedisLock;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import hu.bets.common.util.json.Json;
import hu.bets.points.model.Bet;
import hu.bets.points.model.MatchResult;
import hu.bets.points.model.Result;
import hu.bets.points.model.ToplistEntry;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Consumer;

import static hu.bets.points.dbaccess.DatabaseFields.MATCH_DATE;
import static hu.bets.points.dbaccess.DatabaseFields.MATCH_ID;

public class DefaultScoresServiceDAO implements ScoresServiceDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultScoresServiceDAO.class);

    private static final int UNPROCESSED_MATCHES_COLLECTION = 0;
    private static final int MATCH_RESULTS_COLLECTION = 1;
    private static final Json JSON_UTILS = new Json();

    private MongoCollection<Document> matchCollection;
    private MongoCollection<Document> scoreCollection;
    private MongoCollection<Document> toplistCollection;
    private JedisPool cachePool;

    @Value("${match.cache.expiration.seconds:3000}")
    protected int matchExpiration;
    @Value("${match.retry.threshold.hours:24}")
    protected int retryThreshold;
    @Value("${cache.lock.acquire.expiration.millis:1000}")
    private int lockAcquireExpiration;
    @Value("${cache.lock.expiration.millis:1000}")
    private int lockExpiration;

    public DefaultScoresServiceDAO(MongoCollection<Document> matchCollection,
                                   MongoCollection<Document> scoreCollection,
                                   MongoCollection<Document> toplistCollection,
                                   JedisPool cachePool) {
        this.matchCollection = matchCollection;
        this.scoreCollection = scoreCollection;
        this.toplistCollection = toplistCollection;
        this.cachePool = cachePool;
    }

    @Override
    public void saveMatch(MatchResult matchResult) {
        String recordJson = JSON_UTILS.toJson(matchResult);
        matchCollection.insertOne(Document.parse(recordJson));
        cacheResult(matchResult.getResult().getMatchId(), matchResult.getResult());
    }

    @Override
    public Set<String> getFailedMatchIds() {
        Set<String> unprocessedMatches = getUnprocessedMatches();
        return filterUnprocessedMatches(unprocessedMatches);
    }

    @Override
    public void savePoints(Bet bet, int value) {
        saveEntry(bet, value);
        updateToplist(bet.getUserId(), value);
    }

    private void updateToplist(String userId, int value) {
        Bson filter = Filters.eq("userId", userId);
        Bson update = new BasicDBObject("$inc", new BasicDBObject("points", value)).append(
                "$set", new BasicDBObject("userId", userId));
        UpdateOptions options = new UpdateOptions().upsert(true);

        toplistCollection.updateOne(filter, update, options);
    }

    private void saveEntry(Bet bet, int value) {
        Document betDocument = scoreCollection.find(Filters.eq("betId", bet.getBetId())).first();

        if (betDocument == null) {
            Document toSave = new Document();
            toSave.put("userId", bet.getUserId());
            toSave.put("matchId", bet.getMatchId());
            toSave.put("competitionId", bet.getResult().getCompetitionId());
            toSave.put("betId", bet.getBetId());
            toSave.put("points", value);

            scoreCollection.insertOne(toSave);
        }
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
        try (Jedis jedis = cachePool.getResource()) {
            jedis.select(UNPROCESSED_MATCHES_COLLECTION);

            for (String matchId : unprocessedMatches) {
                jedis.set(matchId, matchId);
            }
        }
    }

    @Override
    public Map<String, Long> getToplistScore(List<String> userIds) {
        Map<String, Long> result = new HashMap<>();

        FindIterable<Document> documents = toplistCollection.find(Filters.in("userId", userIds));
        documents.forEach((Consumer<Document>) document -> {
            ToplistEntry entry = JSON_UTILS.fromJson(document.toJson(), ToplistEntry.class);
            result.put(entry.getUserId(), entry.getPoints());
        });
        LOGGER.info("Resulting values for userIds: {} are: {}", userIds, result);
        return result;
    }

    private void cacheResult(String matchId, Result result) {
        try (Jedis jedis = cachePool.getResource()) {
            jedis.select(MATCH_RESULTS_COLLECTION);
            jedis.setex(matchId, matchExpiration, JSON_UTILS.toJson(result));
        }
    }

    private Optional<Result> findMatchInDatabase(String matchId) {
        Document matchResult = matchCollection.find(Filters.eq(MATCH_ID, matchId)).first();
        if (matchResult == null) {
            return Optional.empty();
        }

        return Optional.of(JSON_UTILS.fromJson(matchResult.toJson(), MatchResult.class).getResult());
    }

    private Optional<Result> findMatchInCache(String matchId) {
        try (Jedis jedis = cachePool.getResource()) {

            jedis.select(MATCH_RESULTS_COLLECTION);
            String json = jedis.get(matchId);
            if (json == null) {
                return Optional.empty();
            }
            return Optional.of(JSON_UTILS.fromJson(json, Result.class));
        }
    }

    private Set<String> filterUnprocessedMatches(Set<String> unprocessedMatches) {

        LocalDateTime thresholdDate = getCurrentTime().minusHours(retryThreshold);

        Bson query = Filters.and(
                Filters.in(MATCH_ID, unprocessedMatches),
                Filters.gte(MATCH_DATE, DateUtil.format(thresholdDate))
        );

        FindIterable<Document> documents = matchCollection.find(query);
        Set<String> result = new HashSet<>();

        documents.forEach((Consumer<Document>) document ->
                result.add(JSON_UTILS.fromJson(document.toJson(), MatchResult.class).getResult().getMatchId()));

        LOGGER.info("Unprocessed messages from the database: " + result);
        return result;
    }

    private Set<String> getUnprocessedMatches() {

        try (Jedis jedis = cachePool.getResource()) {
            JedisLock lock = new JedisLock(jedis, "processingLock", lockAcquireExpiration, lockExpiration);
            Set<String> resultRecords = new HashSet<>();

            try {
                if (lock.acquire()) {
                    jedis.select(UNPROCESSED_MATCHES_COLLECTION);
                    try {
                        resultRecords.addAll(jedis.keys("*"));
                        jedis.flushDB();
                    } catch (Exception e) {
                        lock.release();
                    }
                }
            } catch (InterruptedException e) {
                // Nothing to do here.
            }

            LOGGER.info("Unprocessed match IDs from redis: " + resultRecords);
            return resultRecords;
        }
    }

    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now(ZoneId.of("Europe/London"));
    }
}
