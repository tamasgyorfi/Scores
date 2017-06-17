package hu.bets.points.messaging.sender;

import com.rabbitmq.client.Channel;
import hu.bets.points.messaging.MessagingConstants;
import hu.bets.points.processor.ProcessingResult;
import hu.bets.points.utils.JsonUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
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
                Future<ProcessingResult> payload = resultQueue.poll(1L, TimeUnit.SECONDS);
                if (isPayloadPresent(payload)) {
                    sendMessage(new JsonUtils().toJson(payload.get()));
                }
            } catch (InterruptedException e) {
                shouldContinue = false;
            } catch (Exception e) {
                // Don't let the sender thread die.
                LOGGER.error("Exception while sending message: ", e);
            }
        }
    }

    private boolean isPayloadPresent(Future<ProcessingResult> payload) throws ExecutionException, InterruptedException {
        return payload != null && !payload.get().getPayload().isEmpty();
    }

    private void sendMessage(String payload) {
        LOGGER.info("Sending message to bets service: " + payload);
        for (int i = 0; i < NR_OF_RETRIES; i++) {
            try {
                channel.basicPublish(MessagingConstants.EXCHANGE_NAME,
                        MessagingConstants.SCORES_TO_BETS_ROUTE,
                        null,
                        payload.getBytes());
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