package hu.bets.processor.betbatch;

import hu.bets.model.BetBatch;
import hu.bets.processor.AbstractProcessorTask;
import hu.bets.processor.Type;
import hu.bets.processor.betbatch.processing.BetBatchProcessor;
import hu.bets.processor.betbatch.validation.BetBatchValidator;
import hu.bets.processor.betbatch.validation.InvalidBatchException;
import hu.bets.utils.JsonUtils;

public class BetBatchTask extends AbstractProcessorTask<BetBatch> {

    private final String batchPayload;

    public BetBatchTask(String batchPayload, BetBatchValidator validator, BetBatchProcessor processor) {
        super(batchPayload, validator, processor);
        this.batchPayload = batchPayload;
    }

    @Override
    public BetBatch preProcess() {
        try {
            return getMapper().fromJson(batchPayload, BetBatch.class);
        } catch (Exception e) {
            throw new InvalidBatchException("Batch with payload " + batchPayload + " cannot be read.", e);
        }
    }

    @Override
    public Type getType() {
        return Type.ACKNOWLEDGE_REQUEST;
    }

    // This is here for testability
    protected JsonUtils getMapper() {
        return new JsonUtils();
    }
}
