package hu.bets.points.messaging.receiver;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import hu.bets.points.processor.CommonExecutor;
import hu.bets.points.processor.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class MessageConsumer extends DefaultConsumer {

    private static Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);
    private final CommonExecutor commonExecutor;

    public MessageConsumer(Channel channel, CommonExecutor commonExecutor) {
        super(channel);
        this.commonExecutor = commonExecutor;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope,
                               AMQP.BasicProperties properties, byte[] body) throws IOException {
        String message = new String(body, "UTF-8");
        LOGGER.info("Received message: " + message);
        commonExecutor.enqueue(Optional.of(message), Type.ACKNOWLEDGE_REQUEST);
    }
}
