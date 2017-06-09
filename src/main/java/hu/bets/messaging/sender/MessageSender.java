package hu.bets.messaging.sender;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import hu.bets.messaging.MessagingConstants;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class MessageSender {

    private static final int NR_OF_RETRIES = 3;
    private static final Logger LOGGER = Logger.getLogger(MessageSender.class);

    private volatile boolean shouldContinue = true;

    private Channel channel;
    private BlockingQueue<String> payloadQueue;

    public MessageSender(Channel channel, BlockingQueue<String> payloadQueue) {
        this.channel = channel;
        this.payloadQueue = payloadQueue;
    }

    private void run() {
        while (shouldContinue) {
            try {
                String payload = payloadQueue.poll(5L, TimeUnit.SECONDS);
                if (payload != null) {
                    sendBetAggregateRequest(payload);
                }
            } catch (Exception e) {
                // Nothing to worry about, quit runner thread
                shouldContinue = false;
            }
        }
    }

    private void sendBetAggregateRequest(String payload) {
        LOGGER.info("Sending message to bets service: " + payload);
        for (int i = 0; i < NR_OF_RETRIES; i++) {
            try {
                channel.basicPublish(MessagingConstants.EXCHANGE_NAME, MessagingConstants.SCORES_TO_BETS_ROUTE, buildHeaders(), payload.getBytes());
                break;
            } catch (IOException e) {
                LOGGER.error("Unable to send batch: " + payload, e);
            }
        }
        LOGGER.info("Message sent successfully.");
    }

    private AMQP.BasicProperties buildHeaders() {
        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        Map<String, Object> headers = new HashMap<>();
        headers.put("MESSAGE_TYPE", "AGGREGATION_REQUEST");
        return builder.headers(headers).build();

    }

    public void start() {
        new Thread(this::run).start();
        LOGGER.info("Message sender thread started successfully.");
    }

    public void stop() {
        this.shouldContinue = false;
    }
}