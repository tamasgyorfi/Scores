package integration;

import com.google.common.collect.Sets;
import com.rabbitmq.client.Channel;
import hu.bets.points.config.ApplicationConfig;
import hu.bets.points.config.MessagingConfig;
import hu.bets.points.config.WebConfig;
import hu.bets.points.dbaccess.MongoBasedScoresServiceDAO;
import hu.bets.points.dbaccess.ScoresServiceDAO;
import hu.bets.points.messaging.MessagingConstants;
import hu.bets.steps.Given;
import hu.bets.steps.util.ApplicationContextHolder;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import redis.clients.jedis.JedisPool;
import utils.TestConsumer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static utils.TestUtils.getRecord;

public class RetryTest {

    @BeforeClass
    public static void setup() throws Exception {
        Given.environmentIsUpAndRunning(FakeApplicationConfig.class,
                FakeDatabaseConfig.class,
                MessagingConfig.class,
                WebConfig.class);

        TimeUnit.SECONDS.sleep(2);
    }

    @After
    public void after() {
        ApplicationContextHolder.getBean(JedisPool.class).getResource().flushAll();
        FakeDatabaseConfig.FongoResultsCollectionHolder.getMatchResultCollection().drop();
        FakeDatabaseConfig.FongoResultsCollectionHolder.getScoresCollection().drop();
    }

    @Test
    public void testReply() throws IOException, TimeoutException, InterruptedException {
        MongoBasedScoresServiceDAO matchDAO = (MongoBasedScoresServiceDAO) ApplicationContextHolder.getBean(ScoresServiceDAO.class);

        LocalDateTime in = LocalDateTime.now();

        matchDAO.saveMatch(getRecord(in, "match1"));
        matchDAO.saveMatch(getRecord(in, "match3"));
        matchDAO.saveMatch(getRecord(in, "match4"));
        matchDAO.saveMatch(getRecord(in, "match6"));
        matchDAO.saveMatch(getRecord(in, "match7"));
        matchDAO.saveMatch(getRecord(in, "match10"));

        matchDAO.saveNonProcessedMatches(Sets.newHashSet("match1", "match2", "match9", "match10"));

        Channel receiverChannel = ApplicationContextHolder.getBean(Channel.class);
        TestConsumer testConsumer = new TestConsumer(receiverChannel);
        receiverChannel.queueBind(MessagingConstants.SCORES_TO_BETS_QUEUE, MessagingConstants.EXCHANGE_NAME, MessagingConstants.SCORES_TO_BETS_ROUTE);
        receiverChannel.basicConsume(MessagingConstants.SCORES_TO_BETS_QUEUE, true, testConsumer);

        TimeUnit.SECONDS.sleep(3);
        assertEquals("{\"payload\":[\"match1\",\"match10\"],\"type\":\"BETS_REQUEST\"}", testConsumer.getMessage());

        receiverChannel.close();
    }

    static class FakeApplicationConfig extends ApplicationConfig {
        @Override
        @Bean
        public PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
            PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
            configurer.setFileEncoding("UTF-8");
            configurer.setIgnoreResourceNotFound(false);
            configurer.setLocation(new ClassPathResource("retry_enabled.properties"));

            return configurer;
        }
    }
}
