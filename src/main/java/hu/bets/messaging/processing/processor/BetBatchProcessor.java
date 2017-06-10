package hu.bets.messaging.processing.processor;

import hu.bets.model.BetsBatch;
import hu.bets.model.ProcessingResult;

public interface BetBatchProcessor {

    /**
     * Iterates through a <b>valid</b> {@link BetsBatch} and returns those matchIds which have unprocessed {@link hu.bets.model.Bet}s
     * and the betIDs that need to be acknoledged, packed in a {@link ProcessingResult}.
     *
     * @param betsBatch
     * @return
     */
    ProcessingResult processMatches(BetsBatch betsBatch);
}