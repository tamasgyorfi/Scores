package hu.bets.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import hu.bets.common.config.CommonMessagingConfig;
import hu.bets.common.messaging.DefaultMessageListener;
import hu.bets.common.messaging.MessageListener;
import hu.bets.messaging.receiver.MessageConsumer;
import hu.bets.messaging.sender.MessageSender;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static hu.bets.messaging.MessagingConstants.*;

@Configuration
@Import(CommonMessagingConfig.class)
public class MessagingConfig {

    private static final String MESSAGING_URI = "CLOUDAMQP_URL";
    private static final Logger LOGGER = Logger.getLogger(MessagingConfig.class);

    @Bean
    public MessageConsumer betAggregationRequestListener(Channel channel) {
        return new MessageConsumer(channel);
    }

    @Bean
    public Consumer consumer(Channel channel) {
        return new MessageConsumer(channel);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public MessageSender betAggregateResultSender(Channel channel) {
        return new MessageSender(channel);
    }

    @Bean
    public MessageListener messageListener(Channel channel, Consumer consumer) {
        return new DefaultMessageListener(channel, consumer, AGGREGATE_REQUEST_QUEUE_NAME, EXCHANGE_NAME, AGGREGATE_REQUEST_ROUTING_KEY);
    }

}
