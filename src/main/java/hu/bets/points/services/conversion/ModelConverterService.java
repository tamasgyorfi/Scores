package hu.bets.points.services.conversion;

import hu.bets.points.model.MatchResultWithToken;

public interface ModelConverterService {

    MatchResultWithToken convert(String resultRequest);
}
