package hu.bets.messaging.receiver;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import hu.bets.messaging.processing.BetsBatchExecutor;
import org.apache.log4j.Logger;

import java.io.IOException;

public class MessageConsumer extends DefaultConsumer {

    private static Logger LOGGER = Logger.getLogger(MessageConsumer.class);
    private final BetsBatchExecutor betsBatchExecutor;

    public MessageConsumer(Channel channel, BetsBatchExecutor betsBatchExecutor) {
        super(channel);
        this.betsBatchExecutor = betsBatchExecutor;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope,
                               AMQP.BasicProperties properties, byte[] body) throws IOException {
        String message = new String(body, "UTF-8");
        LOGGER.info("Received message: " + message);
        betsBatchExecutor.enqueue(message);
    }
}
