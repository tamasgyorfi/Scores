package hu.bets.points.processor.betbatch;

import hu.bets.points.model.BetBatch;
import hu.bets.points.processor.AbstractValidatedProcessorTask;
import hu.bets.points.processor.Type;
import hu.bets.points.processor.betbatch.processing.BetBatchProcessor;
import hu.bets.points.processor.betbatch.validation.BetBatchValidator;
import hu.bets.points.processor.betbatch.validation.InvalidBatchException;
import hu.bets.points.utils.JsonUtils;

public class BetBatchTask extends AbstractValidatedProcessorTask<BetBatch> {

    public BetBatchTask(BetBatchValidator validator, BetBatchProcessor processor) {
        super(validator, processor);
    }

    @Override
    public BetBatch preProcess(String payload) {
        try {
            return getMapper().fromJson(payload, BetBatch.class);
        } catch (Exception e) {
            throw new InvalidBatchException("Batch with payload " + payload + " cannot be read.", e);
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
