package integration;

import com.fiftyonred.mock_jedis.MockJedis;
import com.github.fakemongo.Fongo;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class FakeDatabaseConfig {

    private static final class FongoResultsCollectionHolder {

        private static Fongo fongo = new Fongo("database-1");
        private static MongoCollection matchResultCollection = fongo.getDatabase("aaa").getCollection("matchResult");
        private static MongoCollection errorCollection = fongo.getDatabase("aaa").getCollection("error");
        private static MongoCollection scoresCollection = fongo.getDatabase("aaa").getCollection("Scores");

        public static MongoCollection getMatchResultCollection() {
            return matchResultCollection;
        }
    }

    @Bean
    @Qualifier("ResultsCollection")
    public MongoCollection<Document> getResultsCollection() {
        return FongoResultsCollectionHolder.getMatchResultCollection();
    }

    @Bean
    public Jedis geterrorCollection() {
        return new MockJedis("test");
    }

}
