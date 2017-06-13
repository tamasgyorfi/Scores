package hu.bets.processor;

import hu.bets.processor.betbatch.validation.InvalidBatchException;

public interface Validator<T> {

    void validate(T toValidate) throws InvalidBatchException;
}
