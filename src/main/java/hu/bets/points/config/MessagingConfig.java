package hu.bets.points.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import hu.bets.common.config.CommonMessagingConfig;
import hu.bets.common.messaging.DefaultMessageListener;
import hu.bets.common.messaging.MessageListener;
import hu.bets.points.processor.CommonExecutor;
import hu.bets.points.messaging.receiver.MessageConsumer;
import hu.bets.points.messaging.sender.MessageSender;
import hu.bets.points.model.ProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.CompletionService;

import static hu.bets.points.messaging.MessagingConstants.*;

@Configuration
@Import(CommonMessagingConfig.class)
public class MessagingConfig {

    @Autowired
    protected Channel senderChannel;
    @Autowired
    protected Channel receiverChannel;

    @Bean
    public Consumer consumer(CommonExecutor commonExecutor) {
        return new MessageConsumer(receiverChannel, commonExecutor);
    }

    @Bean
    public MessageListener messageListener(Consumer consumer) {
        return new DefaultMessageListener(receiverChannel, consumer, BETS_TO_SCORES_QUEUE, EXCHANGE_NAME, BETS_TO_SCORES_ROUTE);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public MessageSender betAggregateResultSender(CompletionService<ProcessingResult> executor) {
        return new MessageSender(senderChannel, executor);
    }
}
