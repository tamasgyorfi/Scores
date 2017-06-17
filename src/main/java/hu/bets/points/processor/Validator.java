package hu.bets.points.processor;

public interface Validator<T> {

    /**
     * validates a generic payload. May throw any runtime exception when validation fails.
     *
     * @param toValidate
     */
    void validate(T toValidate);
}
