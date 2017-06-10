package hu.bets.points.services.conversion;

import hu.bets.model.FinalMatchResult;

public interface ModelConverterService {

    FinalMatchResult convert(String matchId, String resultRequest);
}
