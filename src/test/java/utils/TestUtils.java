package utils;

import com.google.gson.Gson;
import hu.bets.model.MatchResult;
import hu.bets.model.Result;

import java.time.LocalDateTime;

public class TestUtils {

    public static final String CORRECT_MATCH_END_PAYLOAD = getMatchEndPayload("match100");


    public static MatchResult getRecord(LocalDateTime matchDate, String matchId) {
        return new MatchResult(new Result(matchId, "1", "1", "1", 1, 1), matchDate);
    }

    public static final String getMatchEndPayload(String matchId) {
        return "{  \n" +
                "   \"matchResult\":{  \n" +
                "      \"result\":{  \n" +
                "         \"matchId\":\""+matchId+"\",\n" +
                "         \"competitionId\":\"compId100\",\n" +
                "         \"homeTeamId\":\"team1\",\n" +
                "         \"awayTeamId\":\"team2\",\n" +
                "         \"homeTeamGoals\":1,\n" +
                "         \"awayTeamGoals\":1\n" +
                "      }\n" +
                "   },\n" +
                "   \"securityToken\":\"ashdj\"\n" +
                "}";
    }
}
