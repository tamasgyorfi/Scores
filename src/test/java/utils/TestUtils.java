package utils;

import hu.bets.model.MatchResult;
import hu.bets.model.Result;

import java.time.LocalDateTime;

public class TestUtils {
    public static MatchResult getRecord(LocalDateTime matchDate, String matchId) {
        return new MatchResult(matchId, new Result("1", "1", "1", 1, 1), matchDate);
    }

}
