package hu.bets.messaging.receiver;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import hu.bets.processor.CommonExecutor;
import hu.bets.processor.Type;
import org.apache.log4j.Logger;

import java.io.IOException;

public class MessageConsumer extends DefaultConsumer {

    private static Logger LOGGER = Logger.getLogger(MessageConsumer.class);
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
        commonExecutor.enqueue(message, Type.ACKNOWLEDGE_REQUEST);
    }
}
