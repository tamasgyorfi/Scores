package utils;

import hu.bets.common.util.json.Json;
import hu.bets.points.model.MatchResult;
import hu.bets.points.model.Result;
import hu.bets.points.web.model.ToplistRequestPayload;

import java.time.LocalDateTime;
import java.util.List;

public class TestUtils {

    public static final String CORRECT_MATCH_END_PAYLOAD = getMatchEndPayload("match100");


    public static MatchResult getRecord(LocalDateTime matchDate, String matchId) {
        return new MatchResult(new Result(matchId, "1", "1", "1", 1, 1, matchDate.toString()), matchDate);
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
                "            \"awayTeamGoals\":1,\n" +
                "            \"matchDate\":\"2019-09-09\"\n" +
                "         }\n" +
                "      }\n" +
                "   ],\n" +
                "   \"token\":\"token-to-be-filled\"\n" +
                "}";
    }

    public static final String getToplistPayload(List<String> userIds) {
        return new Json().toJson(new ToplistRequestPayload(userIds, "empty-token"));
    }

}
