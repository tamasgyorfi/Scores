package integration;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import hu.bets.points.config.MessagingConfig;
import hu.bets.points.messaging.receiver.MessageConsumer;
import hu.bets.points.processor.CommonExecutor;
import org.springframework.context.annotation.Bean;
import utils.TestConsumer;

public class FakeMessagingConfig extends MessagingConfig {

    @Bean
    @Override
    public Consumer consumer(CommonExecutor executor) {
        return new TestConsumer(receiverChannel);
    }

}
