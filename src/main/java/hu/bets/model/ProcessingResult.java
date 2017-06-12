package hu.bets.model;

import java.util.Optional;

public class ProcessingResult<T> {

    public enum Type {
        BETS_REQUEST,
        ACKNOWLEDGE_REQUEST
    }

    private final T payload;
    private final Type type;

    public ProcessingResult() {
        payload = null;
        type = null;
    }

    public ProcessingResult(T payload, Type type) {
        this.payload = payload;
        this.type = type;
    }

    public Optional<T> getPayload() {
        return Optional.ofNullable(payload);
    }

    public Type getType() {
        return type;
    }
}
