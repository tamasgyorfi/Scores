package hu.bets.messaging.sender;

import com.rabbitmq.client.Channel;
import hu.bets.messaging.MessagingConstants;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletionService;

public class MessageSender {

    private static final int NR_OF_RETRIES = 3;
    private static final Logger LOGGER = Logger.getLogger(MessageSender.class);

    private volatile boolean shouldContinue = true;

    private Channel channel;

    public MessageSender(Channel channel) {
        this.channel = channel;
    }

    private void run() {
        while (shouldContinue) {
        }
    }

    public void start() {
        new Thread(this::run).start();
        LOGGER.info("Message sender thread started successfully.");
    }

    public void stop() {
        this.shouldContinue = false;
    }
}