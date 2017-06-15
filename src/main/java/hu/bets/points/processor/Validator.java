package hu.bets.points.processor;

import hu.bets.points.processor.betbatch.validation.InvalidBatchException;

public interface Validator<T> {

    void validate(T toValidate) throws InvalidBatchException;
}
