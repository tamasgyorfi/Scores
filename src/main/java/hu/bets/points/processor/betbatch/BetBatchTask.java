package hu.bets.points.processor.betbatch;

import hu.bets.points.model.BetBatch;
import hu.bets.points.processor.AbstractProcessorTask;
import hu.bets.points.processor.Type;
import hu.bets.points.processor.betbatch.processing.BetBatchProcessor;
import hu.bets.points.processor.betbatch.validation.BetBatchValidator;
import hu.bets.points.processor.betbatch.validation.InvalidBatchException;
import hu.bets.points.utils.JsonUtils;

public class BetBatchTask extends AbstractProcessorTask<BetBatch> {

    public BetBatchTask(BetBatchValidator validator, BetBatchProcessor processor) {
        super(validator, processor);
    }

    @Override
    public BetBatch preProcess() {
        try {
            return getMapper().fromJson(getPayload(), BetBatch.class);
        } catch (Exception e) {
            throw new InvalidBatchException("Batch with payload " + getPayload() + " cannot be read.", e);
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
