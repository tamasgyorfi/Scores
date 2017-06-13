package hu.bets.processor.betrequest.processing;

import hu.bets.model.MatchResult;
import hu.bets.processor.Processor;

import java.util.Set;

public interface BetRequestProcessor extends Processor<MatchResult> {

    @Override
    Set<String> process(MatchResult toProcess);
}
