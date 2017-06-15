package hu.bets.points.processor.betrequest.processing;

import hu.bets.points.model.SecureMatchResult;
import hu.bets.points.processor.Processor;

import java.util.Set;

public interface BetRequestProcessor extends Processor<SecureMatchResult> {

    @Override
    Set<String> process(SecureMatchResult toProcess);
}
