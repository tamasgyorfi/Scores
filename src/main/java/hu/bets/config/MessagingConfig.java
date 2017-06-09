package hu.bets.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import hu.bets.common.config.CommonMessagingConfig;
import hu.bets.common.messaging.DefaultMessageListener;
import hu.bets.common.messaging.MessageListener;
import hu.bets.messaging.receiver.MessageConsumer;
import hu.bets.messaging.sender.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.LinkedBlockingDeque;

import static hu.bets.messaging.MessagingConstants.*;

@Configuration
@Import(CommonMessagingConfig.class)
public class MessagingConfig {

    @Autowired
    private Channel senderChannel;
    @Autowired
    private Channel receiverChannel;

    @Bean
    public Consumer consumer() {
        return new MessageConsumer(receiverChannel);
    }

    @Bean
    public MessageListener messageListener(Consumer consumer) {
        return new DefaultMessageListener(receiverChannel, consumer, BETS_TO_SCORES_QUEUE, EXCHANGE_NAME, BETS_TO_SCORES_ROUTE);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public MessageSender betAggregateResultSender() {
        return new MessageSender(senderChannel, new LinkedBlockingDeque<>());
    }
}
