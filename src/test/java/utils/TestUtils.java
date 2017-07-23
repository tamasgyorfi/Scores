package utils;

import hu.bets.points.model.MatchResult;
import hu.bets.points.model.Result;

import java.time.LocalDateTime;

public class TestUtils {

    public static final String CORRECT_MATCH_END_PAYLOAD = getMatchEndPayload("match100");


    public static MatchResult getRecord(LocalDateTime matchDate, String matchId) {
        return new MatchResult(new Result(matchId, "1", "1", "1", 1, 1), matchDate);
    }

    public static final String getMatchEndPayload(String matchId) {
        return "{\n" +
                "   \"results\":[\n" +
                "      {\n" +
                "         \"result\":{\n" +
                "            \"homeTeamName\":\"Kayserispor\",\n" +
                "            \"awayTeamName\":\"Basaksehir FK\",\n" +
                "            \"matchId\":\"" + matchId + "\",\n" +
                "            \"competitionId\":\"Super Lig\",\n" +
                "            \"homeTeamGoals\":0,\n" +
                "            \"awayTeamGoals\":1\n" +
                "         }\n" +
                "      }\n" +
                "   ],\n" +
                "   \"token\":\"token-to-be-filled\"\n" +
                "}";
    }
}
