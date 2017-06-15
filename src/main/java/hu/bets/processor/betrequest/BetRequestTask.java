package hu.bets.processor.betrequest;

import hu.bets.model.SecureMatchResult;
import hu.bets.processor.AbstractProcessorTask;
import hu.bets.processor.Type;
import hu.bets.processor.betrequest.processing.BetRequestProcessor;
import hu.bets.processor.betrequest.validation.BetRequestValidator;

public class BetRequestTask extends AbstractProcessorTask<SecureMatchResult> {

    public BetRequestTask(BetRequestValidator validator, BetRequestProcessor processor) {
        super(validator, processor);
    }

    @Override
    public SecureMatchResult preProcess() {
        return null;
    }

    @Override
    public Type getType() {
        return Type.BETS_REQUEST;
    }
}
