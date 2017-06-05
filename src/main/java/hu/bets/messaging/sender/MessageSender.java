package hu.bets.messaging.sender;

import com.rabbitmq.client.Channel;
import org.apache.log4j.Logger;

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