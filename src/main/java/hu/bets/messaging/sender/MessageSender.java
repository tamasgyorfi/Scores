package hu.bets.messaging.sender;

import com.rabbitmq.client.Channel;
import hu.bets.messaging.MessagingConstants;
import hu.bets.model.ProcessingResult;
import hu.bets.utils.JsonUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class MessageSender {

    private static final int NR_OF_RETRIES = 3;
    private static final Logger LOGGER = Logger.getLogger(MessageSender.class);

    private volatile boolean shouldContinue = true;

    private Channel channel;
    private CompletionService<ProcessingResult> resultQueue;

    public MessageSender(Channel channel, CompletionService<ProcessingResult> resultQueue) {
        this.channel = channel;
        this.resultQueue = resultQueue;
    }

    private void run() {
        while (shouldContinue) {
            try {
                Future<ProcessingResult> payload = resultQueue.poll(5L, TimeUnit.SECONDS);
                if (payload != null) {
                    sendMessage(new JsonUtils().toJson(payload.get()));
                }
            } catch (Exception e) {
                // Nothing to worry about, quit runner thread
                shouldContinue = false;
            }
        }
    }

    private void sendMessage(String payload) {
        LOGGER.info("Sending message to bets service: " + payload);
        for (int i = 0; i < NR_OF_RETRIES; i++) {
            try {
                channel.basicPublish(MessagingConstants.EXCHANGE_NAME, MessagingConstants.SCORES_TO_BETS_ROUTE, null, payload.getBytes());
                break;
            } catch (IOException e) {
                LOGGER.error("Unable to send batch: " + payload, e);
            }
        }
        LOGGER.info("Message sent successfully.");
    }

    public void start() {
        new Thread(this::run).start();
        LOGGER.info("Message sender thread started successfully.");
    }

    public void stop() {
        this.shouldContinue = false;
    }
}