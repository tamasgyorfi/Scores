package hu.bets.processor.betrequest.validation;

import hu.bets.model.SecureMatchResult;
import hu.bets.processor.Validator;

public interface BetRequestValidator extends Validator<SecureMatchResult> {
    @Override
    void validate(SecureMatchResult matchResult);
}
