package hu.bets.model;

import hu.bets.points.dbaccess.DateUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MatchResult {

    private static final ZoneId ZONE_ID = ZoneId.of("Europe/London");

    private final String matchId;
    private final String matchDate;
    private final Result result;

    public MatchResult(String matchId, Result result, LocalDateTime matchDate) {
        this.matchId = matchId;
        this.matchDate = DateUtil.format(matchDate);
        this.result = result;
    }

    public MatchResult(String matchId, Result result) {
        this.matchId = matchId;
        this.matchDate = DateUtil.format(LocalDateTime.now(ZONE_ID));
        this.result = result;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public Result getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "MatchResult{" +
                "matchId='" + matchId + '\'' +
                ", matchDate=" + matchDate +
                ", result=" + result +
                '}';
    }
}
