package hu.bets.processor.betbatch.validation;

import hu.bets.common.util.hash.HashGenerator;
import hu.bets.model.BetBatch;

public class DefaultBetBatchValidator implements BetBatchValidator {

    private HashGenerator hashGenerator;

    public DefaultBetBatchValidator(HashGenerator hashGenerator) {
        this.hashGenerator = hashGenerator;
    }

    public void validate(BetBatch betBatch) {
        if (isHashMismatch(betBatch) || isElementsMismatch(betBatch)) {
            throw new InvalidBatchException("Invalid batch received. Hash or size mismatch.");
        }
    }

    private boolean isElementsMismatch(BetBatch betBatch) {
        return betBatch.getNumberOfElements() != betBatch.getBets().size();
    }

    private boolean isHashMismatch(BetBatch betBatch) {
        return !betBatch.getHash().equals(hashGenerator.getHash(betBatch.getBets()));
    }

}
