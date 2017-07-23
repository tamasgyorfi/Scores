package hu.bets.points.processor.betrequest.validation;

import hu.bets.points.model.MatchResultWithToken;
import hu.bets.points.processor.Validator;

public interface BetRequestValidator extends Validator<MatchResultWithToken> {
    @Override
    void validate(MatchResultWithToken matchResult);
}
