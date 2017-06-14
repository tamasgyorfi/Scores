package hu.bets.processor.betrequest;

import hu.bets.model.SecureMatchResult;
import hu.bets.processor.AbstractProcessorTask;
import hu.bets.processor.Processor;
import hu.bets.processor.Type;
import hu.bets.processor.Validator;

public class BetRequestTask extends AbstractProcessorTask<SecureMatchResult> {

    private String payload;

    public BetRequestTask(String payload, Validator<SecureMatchResult> validator, Processor<SecureMatchResult> processor) {
        super(payload, validator, processor);
        this.payload = payload;
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
