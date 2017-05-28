package hu.bets.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import hu.bets.utils.EnvironmentVarResolver;
import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {

    private static final String DATABASE_NAME = "heroku_d2039chx";
    private static final String COLLECTION_NAME = "Scores";
    private static final String DB_URI_KEY = "MONGODB_URI";

    @Bean
    public static MongoCollection<Document> getMongoClient() {
        String dbUri = EnvironmentVarResolver.getEnvVar(DB_URI_KEY);

        MongoClientURI clientURI = new MongoClientURI(dbUri);
        MongoClient client = new MongoClient(clientURI);

        MongoDatabase database = client.getDatabase(DATABASE_NAME);
        return database.getCollection(COLLECTION_NAME);
    }
}
