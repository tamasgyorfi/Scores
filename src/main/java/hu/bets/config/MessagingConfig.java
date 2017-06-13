package hu.bets.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import hu.bets.common.config.CommonMessagingConfig;
import hu.bets.common.messaging.DefaultMessageListener;
import hu.bets.common.messaging.MessageListener;
import hu.bets.processor.CommonExecutor;
import hu.bets.processor.betprocessing.BetBatchProcessor;
import hu.bets.processor.betprocessing.validation.BetBatchValidator;
import hu.bets.messaging.receiver.MessageConsumer;
import hu.bets.messaging.sender.MessageSender;
import hu.bets.processor.ProcessingResult;
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

    @Bean
    public CommonExecutor getBetsBatchExecutor(CompletionService<ProcessingResult> completionService, BetBatchProcessor processor, BetBatchValidator validator) {
        return new CommonExecutor(completionService, processor, validator);
    }
}
