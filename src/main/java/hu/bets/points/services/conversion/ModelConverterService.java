package hu.bets.points.services.conversion;

import hu.bets.model.MatchResult;

public interface ModelConverterService {

    MatchResult convert(String matchId, String resultRequest);
}
