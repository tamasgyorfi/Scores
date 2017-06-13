package hu.bets.processor.betprocessing.validation;

import hu.bets.model.BetsBatch;

public interface BetBatchValidator {

    /**
     * Checks if the betsBatch received as parameter is a valid {@link BetsBatch}. Throws {@link InvalidBatchException}
     * otherwise.
     *
     * @param betsBatch
     */
    void validateBatch(BetsBatch betsBatch) throws InvalidBatchException;
}
