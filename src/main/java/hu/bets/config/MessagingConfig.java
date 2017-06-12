package hu.bets.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import hu.bets.common.config.CommonMessagingConfig;
import hu.bets.common.messaging.DefaultMessageListener;
import hu.bets.common.messaging.MessageListener;
import hu.bets.messaging.processing.BetsBatchExecutor;
import hu.bets.messaging.processing.processor.BetBatchProcessor;
import hu.bets.messaging.processing.validation.BetBatchValidator;
import hu.bets.messaging.receiver.MessageConsumer;
import hu.bets.messaging.sender.MessageSender;
import hu.bets.model.ProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.CompletionService;

import static hu.bets.messaging.MessagingConstants.*;

@Configuration
@Import(CommonMessagingConfig.class)
public class MessagingConfig {

    @Autowired
    private Channel senderChannel;
    @Autowired
    private Channel receiverChannel;

    @Bean
    public Consumer consumer(BetsBatchExecutor betsBatchExecutor) {
        return new MessageConsumer(receiverChannel, betsBatchExecutor);
    }

    @Bean
    public MessageListener messageListener(Consumer consumer) {
        return new DefaultMessageListener(receiverChannel, consumer, BETS_TO_SCORES_QUEUE, EXCHANGE_NAME, BETS_TO_SCORES_ROUTE);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public MessageSender betAggregateResultSender(CompletionService<ProcessingResult> executor) {
        return new MessageSender(senderChannel, executor);
    }

    @Bean
    public BetsBatchExecutor getBetsBatchExecutor(CompletionService<ProcessingResult> completionService, BetBatchProcessor processor, BetBatchValidator validator) {
        return new BetsBatchExecutor(completionService, processor, validator);
    }
}
