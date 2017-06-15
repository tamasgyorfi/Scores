package hu.bets.points.processor.betrequest.validation;

import hu.bets.points.model.SecureMatchResult;
import hu.bets.points.processor.Validator;

public interface BetRequestValidator extends Validator<SecureMatchResult> {
    @Override
    void validate(SecureMatchResult matchResult);
}
