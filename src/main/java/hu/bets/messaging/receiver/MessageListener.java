package hu.bets.messaging.receiver;

public interface MessageListener {

    void receive() throws Exception;
}
