package hu.bets.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hu.bets.points.dbaccess.DateUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FinalMatchResult {

    private static final ZoneId ZONE_ID = ZoneId.of("Europe/London");

    private final String matchId;
    private final String matchDate;
    private final Result result;

    private FinalMatchResult() {
        matchId = null;
        matchDate = null;
        result = null;
    }

    public FinalMatchResult(String matchId, Result result, LocalDateTime matchDate) {
        this.matchId = matchId;
        this.matchDate = DateUtil.format(matchDate);
        this.result = result;
    }

    public FinalMatchResult(String matchId, Result result) {
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
        return "FinalMatchResult{" +
                "matchId='" + matchId + '\'' +
                ", matchDate=" + matchDate +
                ", result=" + result +
                '}';
    }
}
