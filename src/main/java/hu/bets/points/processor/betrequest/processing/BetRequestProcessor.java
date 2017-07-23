package hu.bets.points.processor.betrequest.processing;

import hu.bets.points.model.MatchResultWithToken;
import hu.bets.points.processor.Processor;

import java.util.Set;

public interface BetRequestProcessor extends Processor<MatchResultWithToken> {

    @Override
    Set<String> process(MatchResultWithToken toProcess);
}
