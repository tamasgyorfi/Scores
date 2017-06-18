package hu.bets.points.model;

import hu.bets.points.processor.Type;

import java.util.Set;

public class ProcessingResult {

    private final Set<String> payload;
    private final Type type;

    public ProcessingResult() {
        payload = null;
        type = null;
    }

    public ProcessingResult(Set<String> payload, Type type) {
        this.payload = payload;
        this.type = type;
    }

    public Set<String> getPayload() {
        return payload;
    }

    public String getType() {
        return type.getType();
    }

    @Override
    public String toString() {
        return "ProcessingResult{" +
                "payload=" + payload +
                ", type=" + type +
                '}';
    }
}
