package hu.bets.points.services.conversion;

import hu.bets.points.model.SecureMatchResult;

public interface ModelConverterService {

    SecureMatchResult convert(String matchId, String resultRequest);
}
