package hu.bets.points.services;

public class MatchResultProcessingException extends RuntimeException {

    public MatchResultProcessingException(RuntimeException e) {
        super(e);
    }
}
