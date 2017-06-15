package hu.bets.points.processor.betbatch.validation;

public class InvalidBatchException extends RuntimeException {

    public InvalidBatchException(String s) {
        super(s);
    }

    public InvalidBatchException(String s, Exception e) {
        super(s, e);
    }
}
