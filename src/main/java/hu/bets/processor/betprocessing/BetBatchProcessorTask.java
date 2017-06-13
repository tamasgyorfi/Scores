package hu.bets.processor.betprocessing;

import hu.bets.processor.Type;
import hu.bets.processor.betprocessing.validation.BetBatchValidator;
import hu.bets.processor.betprocessing.validation.InvalidBatchException;
import hu.bets.model.BetsBatch;
import hu.bets.processor.ProcessingResult;
import hu.bets.utils.JsonUtils;
import org.apache.log4j.Logger;

import java.util.concurrent.Callable;

public class BetBatchProcessorTask implements Callable<ProcessingResult> {

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
    public ProcessingResult call() {
        try {
            BetsBatch betsBatch = getBatch(batchPayload);
            defaultBetBatchValidator.validateBatch(betsBatch);
            return new ProcessingResult(betBatchProcessor.processMatches(betsBatch), Type.ACKNOWLEDGE_REQUEST);
        } catch (InvalidBatchException e) {
            LOGGER.error("Unable to process batch.", e);
        }
        return new ProcessingResult();
    }

    private BetsBatch getBatch(String batchPayload) {
        try {
            return getMapper().fromJson(batchPayload, BetsBatch.class);
        } catch (Exception e) {
            throw new InvalidBatchException("Batch with payload " + batchPayload + " cannot be read.", e);
        }
    }

    // This is here for testability
    protected JsonUtils getMapper() {
        return new JsonUtils();
    }
}
