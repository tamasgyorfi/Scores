package hu.bets.processor.betbatch.validation;

import hu.bets.model.BetBatch;
import hu.bets.processor.Validator;

public interface BetBatchValidator extends Validator<BetBatch> {

    /**
     * Checks if the betBatch received as parameter is a valid {@link BetBatch}. Throws {@link InvalidBatchException}
     * otherwise.
     *
     * @param betBatch
     */
    void validate(BetBatch betBatch) throws InvalidBatchException;
}
