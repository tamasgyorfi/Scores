package utils;

import hu.bets.model.FinalMatchResult;
import hu.bets.model.Result;

import java.time.LocalDateTime;

public class TestUtils {
    public static FinalMatchResult getRecord(LocalDateTime matchDate, String matchId) {
        return new FinalMatchResult(matchId, new Result("1", "1", "1", 1, 1), matchDate);
    }

}
