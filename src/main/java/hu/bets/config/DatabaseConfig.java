package hu.bets.config;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import hu.bets.common.config.CommonMongoConfig;
import hu.bets.common.config.model.MongoDetails;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

@Configuration
@Import(CommonMongoConfig.class)
public class DatabaseConfig {

    private static final String DATABASE_NAME = "heroku_d2039chx";
    private static final String SCORES_COLLECTION_NAME = "Scores";
    private static final String RESULTS_COLLECTION_NAME = "Results";

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

}