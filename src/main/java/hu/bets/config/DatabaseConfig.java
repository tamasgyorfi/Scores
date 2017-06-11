package hu.bets.config;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import hu.bets.common.config.CommonMongoConfig;
import hu.bets.common.config.model.MongoDetails;
import hu.bets.utils.EnvironmentVarResolver;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import redis.clients.jedis.Jedis;

@Configuration
@Import(CommonMongoConfig.class)
public class DatabaseConfig {

    private static final String DATABASE_NAME = "heroku_d2039chx";
    private static final String SCORES_COLLECTION_NAME = "Scores";
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
    @Qualifier("ErrorCollection")
    public MongoCollection<Document> errorCollection(MongoDatabase mongoDatabase) {
        return mongoDatabase.getCollection(ERROR_COLLECTION_NAME);
    }

    @Bean
    public Jedis getJedis() {
        return new Jedis(EnvironmentVarResolver.getEnvVar(REDIS_URL));
    }
}