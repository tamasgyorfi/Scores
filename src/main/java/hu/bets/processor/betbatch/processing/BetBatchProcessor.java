package hu.bets.processor.betbatch.processing;

import hu.bets.model.BetBatch;
import hu.bets.processor.Processor;

import java.util.Set;

public interface BetBatchProcessor extends Processor<BetBatch>{

    /**
     * Iterates through a <b>valid</b> {@link BetBatch} and returns those matchIds which have unprocessed {@link hu.bets.model.Bet}s
     * and the betIDs that need to be acknoledged, packed in a {@link Set<String>}.
     *
     * @param betBatch
     * @return
     */
    Set<String> process(BetBatch betBatch);
}
