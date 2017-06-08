package integration;

import com.fiftyonred.mock_jedis.MockJedis;
import com.github.fakemongo.Fongo;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
        try {
            return new Jedis(getJedisEndpoint());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private String getJedisEndpoint() throws Exception {
        Path path = Paths.get(this.getClass().getClassLoader().getResource("redis_pass.txt"). toURI());
        List<String> redisSecrets = Files.readAllLines(path);

        String password = redisSecrets.get(0);
        String url = redisSecrets.get(1);

        return "redis://rediscloud:"+password+"@"+url;
    }

}
