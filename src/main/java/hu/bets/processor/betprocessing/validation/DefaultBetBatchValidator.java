package hu.bets.processor.betprocessing.validation;

import hu.bets.common.util.hash.HashGenerator;
import hu.bets.model.BetsBatch;

public class DefaultBetBatchValidator implements BetBatchValidator {

    private HashGenerator hashGenerator;

    public DefaultBetBatchValidator(HashGenerator hashGenerator) {
        this.hashGenerator = hashGenerator;
    }

    public void validateBatch(BetsBatch betsBatch) {
        if (isHashMismatch(betsBatch) || isElementsMismatch(betsBatch)) {
            throw new InvalidBatchException("Invalid batch received. Hash or size mismatch.");
        }
    }

    private boolean isElementsMismatch(BetsBatch betsBatch) {
        return betsBatch.getNumberOfElements() != betsBatch.getBets().size();
    }

    private boolean isHashMismatch(BetsBatch betsBatch) {
        return !betsBatch.getHash().equals(hashGenerator.getHash(betsBatch.getBets()));
    }

}
