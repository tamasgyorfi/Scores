package hu.bets.points.web.api;

public class IllegalPayloadException extends RuntimeException {

    public IllegalPayloadException(String s) {
        super(s);
    }
}
