package hu.bets.points.dbaccess;

import com.github.jedis.lock.JedisLock;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import hu.bets.points.model.Bet;
import hu.bets.points.model.MatchResult;
import hu.bets.points.model.Result;
import hu.bets.points.utils.JsonUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static hu.bets.points.dbaccess.DatabaseFields.MATCH_DATE;
import static hu.bets.points.dbaccess.DatabaseFields.MATCH_ID;

public class MongoBasedScoresServiceDAO implements ScoresServiceDAO {

    private static final Logger LOGGER = Logger.getLogger(MongoBasedScoresServiceDAO.class);

    private static final int UNPROCESSED_MATCHES_COLLECTION = 0;
    private static final int MATCH_RESULTS_COLLECTION = 1;
    private static final JsonUtils JSON_UTILS = new JsonUtils();

    private MongoCollection<Document> matchCollection;
    private MongoCollection<Document> scoreCollection;
    private JedisPool cachePool;

    @Value("${match.cache.expiration.seconds:3000}")
    protected int matchExpiration;
    @Value("${match.retry.threshold.hours:24}")
    protected int retryThreshold;
    @Value("${cache.lock.acquire.expiration.millis:1000}")
    private int lockAcquireExpiration;
    @Value("${cache.lock.expiration.millis:1000}")
    private int lockExpiration;

    public MongoBasedScoresServiceDAO(MongoCollection<Document> matchCollection, MongoCollection<Document> scoreCollection, JedisPool cachePool) {
        this.matchCollection = matchCollection;
        this.scoreCollection = scoreCollection;
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
        try (Jedis jedis = cachePool.getResource()) {
            jedis.select(UNPROCESSED_MATCHES_COLLECTION);

            for (String matchId : unprocessedMatches) {
                jedis.set(matchId, matchId);
            }
        }
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
