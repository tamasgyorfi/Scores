package hu.bets.points.services.conversion;

import hu.bets.model.MatchResult;
import hu.bets.model.SecureMatchResult;

public interface ModelConverterService {

    SecureMatchResult convert(String matchId, String resultRequest);
}
