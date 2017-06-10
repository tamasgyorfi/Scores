package hu.bets.messaging.processing.processor;

public class BatchProcessingException extends RuntimeException {

    public BatchProcessingException(Exception e) {
        super(e);
    }

    public BatchProcessingException(String s) {
        super(s);
    }

    public BatchProcessingException(String s, Exception e) {
        super(s, e);
    }
}
