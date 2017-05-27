package hu.bets.messaging.receiver;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static hu.bets.messaging.MessagingConstants.AGGREGATE_REQUEST_ROUTING_KEY;
import static hu.bets.messaging.MessagingConstants.EXCHANGE_NAME;

public class BetsListener implements MessageListener {

    private final Channel channel;
    private final Consumer consumer;
    private String queueName;

    public BetsListener(Channel channel, Consumer consumer, String queueName) {
        this.channel = channel;
        this.consumer = consumer;
        this.queueName = queueName;
    }

    @Override
    public void receive() throws IOException, TimeoutException {
        channel.queueBind(queueName, EXCHANGE_NAME, AGGREGATE_REQUEST_ROUTING_KEY);
        channel.basicConsume(queueName, true, consumer);
    }
}
