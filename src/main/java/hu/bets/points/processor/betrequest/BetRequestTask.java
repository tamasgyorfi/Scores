package hu.bets.points.processor.betrequest;

import hu.bets.points.model.MatchResultWithToken;
import hu.bets.points.processor.AbstractValidatedProcessorTask;
import hu.bets.points.processor.Type;
import hu.bets.points.processor.betbatch.validation.InvalidBatchException;
import hu.bets.points.processor.betrequest.processing.BetRequestProcessor;
import hu.bets.points.processor.betrequest.validation.BetRequestValidator;
import hu.bets.points.utils.JsonUtils;

public class BetRequestTask extends AbstractValidatedProcessorTask<MatchResultWithToken> {

    public BetRequestTask(BetRequestValidator validator, BetRequestProcessor processor) {
        super(validator, processor);
    }

    @Override
    public MatchResultWithToken preProcess(String payload) {
        try {
            return getMapper().fromJson(payload, MatchResultWithToken.class);
        } catch (Exception e) {
            throw new InvalidBatchException("Batch with payload " + payload + " cannot be read.", e);
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
