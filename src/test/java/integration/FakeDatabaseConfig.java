package integration;

import com.github.fakemongo.Fongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class FakeDatabaseConfig {

    private static final class FongoResultsCollectionHolder {

        private static MongoDatabase fongo = new Fongo("database-1").getDatabase("aaa");
        private static MongoCollection matchResultCollection = fongo.getCollection("matchResult");
        private static MongoCollection scoresCollection = fongo.getCollection("Scores");

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
        Path path = getFilePath();
        List<String> redisSecrets = Files.readAllLines(path);

        String password = redisSecrets.get(0);
        String url = redisSecrets.get(1);

        return "redis://rediscloud:" + password + "@" + url;
    }

    private Path getFilePath() throws Exception {
        URL resource = this.getClass().getClassLoader().getResource("redis_pass.txt");
        if (resource != null) {
            return Paths.get(resource.toURI());
        } else {
            return Paths.get(System.getProperty("user.home") + "/redis_pass.txt");
        }
    }

}
