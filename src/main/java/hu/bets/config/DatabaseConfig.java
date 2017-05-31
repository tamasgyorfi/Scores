package hu.bets.config;

import hu.bets.common.config.CommonMongoConfig;
import hu.bets.common.config.model.MongoDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CommonMongoConfig.class)
public class DatabaseConfig {

    private static final String DATABASE_NAME = "heroku_d2039chx";
    private static final String COLLECTION_NAME = "Scores";

    @Bean
    public MongoDetails mongoDetails() {
        return new MongoDetails(DATABASE_NAME, COLLECTION_NAME);
    }
}
