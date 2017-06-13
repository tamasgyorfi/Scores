package utils;

import hu.bets.model.MatchResult;
import hu.bets.model.Result;

import java.time.LocalDateTime;

public class TestUtils {

    public static final String CORRECT_MATCH_END_PAYLOAD = "{  \n" +
            "   \"matchResult\":{  \n" +
            "      \"result\":{  \n" +
            "         \"competitionId\":\"compId100\",\n" +
            "         \"homeTeamId\":\"team1\",\n" +
            "         \"awayTeamId\":\"team2\",\n" +
            "         \"homeTeamGoals\":1,\n" +
            "         \"awayTeamGoals\":1\n" +
            "      }\n" +
            "   },\n" +
            "   \"securityToken\":\"ashdj\"\n" +
            "}";


    public static MatchResult getRecord(LocalDateTime matchDate, String matchId) {
        return new MatchResult(matchId, new Result("1", "1", "1", 1, 1), matchDate);
    }

}
