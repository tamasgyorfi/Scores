package hu.bets.messaging.receiver;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.apache.log4j.Logger;

import java.io.IOException;

public class MessageConsumer extends DefaultConsumer {

    private static final String MESSAGE_TYPE_KEY = "MESSAGE_TYPE";
    private static Logger LOGGER = Logger.getLogger(MessageConsumer.class);

    public MessageConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope,
                               AMQP.BasicProperties properties, byte[] body) throws IOException {
        String message = new String(body, "UTF-8");
        LOGGER.info("Received message: " + message);

    }
}
