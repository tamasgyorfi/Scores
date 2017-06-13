package hu.bets.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hu.bets.points.dbaccess.DateUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchResult {

    private static final ZoneId ZONE_ID = ZoneId.of("Europe/London");

    private final String matchDate;
    private final Result result;

    private MatchResult() {
        matchDate = null;
        result = null;
    }

    public MatchResult(Result result, LocalDateTime matchDate) {
        this.matchDate = DateUtil.format(matchDate);
        this.result = result;
    }

    public MatchResult(String matchId, Result result) {
        this.matchDate = DateUtil.format(LocalDateTime.now(ZONE_ID));
        this.result = result;
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
                "matchDate=" + matchDate +
                ", result=" + result +
                '}';
    }
}
