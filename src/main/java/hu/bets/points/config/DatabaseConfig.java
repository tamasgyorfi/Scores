package hu.bets.points.config;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import hu.bets.common.config.CommonMongoConfig;
import hu.bets.points.utils.EnvironmentVarResolver;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;

@Configuration
@Import(CommonMongoConfig.class)
public class DatabaseConfig {

    private static final String DATABASE_NAME = "heroku_d2039chx";
    private static final String SCORES_COLLECTION_NAME = "Scores";
    private static final String TOPLIST_COLLECTION_NAME = "Toplist";
    private static final String ERROR_COLLECTION_NAME = "Error";
    private static final String RESULTS_COLLECTION_NAME = "Results";
    private static final String REDIS_URL = "REDIS_URL";

    @Bean
    @Qualifier("mongoDBName")
    public String mongoDbName() {
        return DATABASE_NAME;
    }

    @Bean
    @Qualifier("ScoresCollection")
    public MongoCollection<Document> scoresCollection(MongoDatabase mongoDatabase) {
        return mongoDatabase.getCollection(SCORES_COLLECTION_NAME);
    }

    @Bean
    @Qualifier("ResultsCollection")
    public MongoCollection<Document> resultsCollection(MongoDatabase mongoDatabase) {
        return mongoDatabase.getCollection(RESULTS_COLLECTION_NAME);
    }

    @Bean
    @Qualifier("ToplistCollection")
    public MongoCollection<Document> toplistCollection(MongoDatabase mongoDatabase) {
        return mongoDatabase.getCollection(TOPLIST_COLLECTION_NAME);
    }

    @Bean
    @Qualifier("ErrorCollection")
    public MongoCollection<Document> errorCollection(MongoDatabase mongoDatabase) {
        return mongoDatabase.getCollection(ERROR_COLLECTION_NAME);
    }

    @Bean
    public JedisPool jedisPool() throws Exception {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(124);
        return new JedisPool(poolConfig, new URI(EnvironmentVarResolver.getEnvVar(REDIS_URL)));
    }

}