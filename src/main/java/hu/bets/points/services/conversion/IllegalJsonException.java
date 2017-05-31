package hu.bets.points.services.conversion;

public class IllegalJsonException extends RuntimeException {

    public IllegalJsonException(Exception e) {
        super(e);
    }
}
