package utils;

import hu.bets.model.MatchResult;
import hu.bets.model.Result;

import java.time.LocalDateTime;

public class TestUtils {

    public static final String CORRECT_MATCH_END_PAYLOAD = "{\n" +
            "  \"awayTeamGoals\": 0,\n" +
            "  \"awayTeamId\": \"1001\",\n" +
            "  \"competitionId\": \"champions\",\n" +
            "  \"homeTeamGoals\": 2,\n" +
            "  \"homeTeamId\": \"2063\"\n" +
            "}";


    public static MatchResult getRecord(LocalDateTime matchDate, String matchId) {
        return new MatchResult(matchId, new Result("1", "1", "1", 1, 1), matchDate);
    }

}
