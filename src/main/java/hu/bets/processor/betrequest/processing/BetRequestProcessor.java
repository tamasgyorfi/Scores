package hu.bets.processor.betrequest.processing;

import hu.bets.model.SecureMatchResult;
import hu.bets.processor.Processor;

import java.util.Set;

public interface BetRequestProcessor extends Processor<SecureMatchResult> {

    @Override
    Set<String> process(SecureMatchResult toProcess);
}
