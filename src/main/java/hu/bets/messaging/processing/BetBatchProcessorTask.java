package hu.bets.messaging.processing;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.bets.messaging.processing.processor.BetBatchProcessor;
import hu.bets.messaging.processing.validation.BetBatchValidator;
import hu.bets.messaging.processing.validation.InvalidBatchException;
import hu.bets.model.BetsBatch;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;

public class BetBatchProcessorTask implements Callable<Set<String>> {

    private static final Logger LOGGER = Logger.getLogger(BetBatchProcessorTask.class);

    private String batchPayload;
    private BetBatchProcessor betBatchProcessor;
    private BetBatchValidator defaultBetBatchValidator;

    public BetBatchProcessorTask(String batchPayload, BetBatchProcessor betBatchProcessor, BetBatchValidator defaultBetBatchValidator) {
        this.batchPayload = batchPayload;
        this.betBatchProcessor = betBatchProcessor;
        this.defaultBetBatchValidator = defaultBetBatchValidator;
    }

    @Override
    public Set<String> call() {
        try {
            BetsBatch betsBatch = getBatch(batchPayload);
            defaultBetBatchValidator.validateBatch(betsBatch);
            return betBatchProcessor.processMatches(betsBatch);
        } catch (InvalidBatchException e) {
            LOGGER.error("Unable to process batch.", e);
        }
        return Collections.emptySet();
    }

    private BetsBatch getBatch(String batchPayload) {
        try {
            return getMapper().readValue(batchPayload, BetsBatch.class);
        } catch (Exception e) {
            throw new InvalidBatchException("Batch with payload " + batchPayload + " cannot be read.", e);
        }
    }

    // This is here for testability
    protected ObjectMapper getMapper() {
        return new ObjectMapper();
    }
}
