package integration;

import com.github.fakemongo.Fongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class FakeDatabaseConfig {

    static final class FongoResultsCollectionHolder {

        private static MongoDatabase fongo = new Fongo("database-1").getDatabase("aaa");
        private static MongoCollection matchResultCollection = fongo.getCollection("matchResult");
        private static MongoCollection scoresCollection = fongo.getCollection("Scores");
        private static MongoCollection toplistCollection = fongo.getCollection("Toplist");


        public static MongoCollection getMatchResultCollection() {
            return matchResultCollection;
        }

        public static MongoCollection getScoresCollection() {
            return scoresCollection;
        }

        public static MongoCollection getToplistCollection() {
            return toplistCollection;
        }

    }

    @Bean
    @Qualifier("ResultsCollection")
    public MongoCollection<Document> getResultsCollection() {
        return FongoResultsCollectionHolder.getMatchResultCollection();
    }

    @Bean
    @Qualifier("ScoresCollection")
    public MongoCollection<Document> getScoresCollection() {
        return FongoResultsCollectionHolder.getScoresCollection();
    }

    @Bean
    @Qualifier("ToplistCollection")
    public MongoCollection<Document> getToplistCollection() {
        return FongoResultsCollectionHolder.getToplistCollection();
    }

    @Bean
    public JedisPool jedisPool() throws Exception {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        return new JedisPool(poolConfig, new URI(getJedisEndpoint()));
    }

    private String getJedisEndpoint() throws Exception {
        Path path = getFilePath();
        List<String> redisSecrets = Files.readAllLines(path);

        String password = redisSecrets.get(0);
        String url = redisSecrets.get(1);

        return "redis://h:" + password + "@" + url;
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
