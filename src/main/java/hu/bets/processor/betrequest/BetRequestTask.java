package hu.bets.processor.betrequest;

import hu.bets.model.BetBatch;
import hu.bets.model.SecureMatchResult;
import hu.bets.processor.AbstractProcessorTask;
import hu.bets.processor.Type;
import hu.bets.processor.betbatch.validation.InvalidBatchException;
import hu.bets.processor.betrequest.processing.BetRequestProcessor;
import hu.bets.processor.betrequest.validation.BetRequestValidator;
import hu.bets.utils.JsonUtils;

public class BetRequestTask extends AbstractProcessorTask<SecureMatchResult> {

    public BetRequestTask(BetRequestValidator validator, BetRequestProcessor processor) {
        super(validator, processor);
    }

    @Override
    public SecureMatchResult preProcess() {
        try {
            return getMapper().fromJson(getPayload(), SecureMatchResult.class);
        } catch (Exception e) {
            throw new InvalidBatchException("Batch with payload " + getPayload() + " cannot be read.", e);
        }
    }

    // This is here for testability
    protected JsonUtils getMapper() {
        return new JsonUtils();
    }

    @Override
    public Type getType() {
        return Type.BETS_REQUEST;
    }
}
