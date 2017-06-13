package hu.bets.processor.betrequest;

import hu.bets.model.MatchResult;
import hu.bets.processor.*;

public class BetRequestTask extends AbstractProcessorTask<MatchResult> {

    private String payload;

    public BetRequestTask(String payload, Validator<MatchResult> validator, Processor<MatchResult> processor) {
        super(payload, validator, processor);
        this.payload = payload;
    }

    @Override
    public MatchResult preProcess() {
        return null;
    }

    @Override
    public Type getType() {
        return Type.BETS_REQUEST;
    }
}
