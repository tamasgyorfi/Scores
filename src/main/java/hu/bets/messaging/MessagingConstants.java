package hu.bets.messaging;

public class MessagingConstants {

    public static final String EXCHANGE_NAME = "amq.direct";

    public static final String SCORES_TO_BETS_QUEUE = "SCORES_TO_BETS";
    public static final String BETS_TO_SCORES_QUEUE = "BETS_TO_SCORES";

    public static final String AGGREGATE_RESPONSE_ROUTING_KEY = "in.bets.request";
    public static final String AGGREGATE_REQUEST_ROUTING_KEY = "in.bets.request";

}
