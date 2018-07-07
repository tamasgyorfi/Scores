package hu.bets.points.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hu.bets.points.dbaccess.DateUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchResult {

    private static final ZoneId ZONE_ID = ZoneId.of("Europe/London");

    private final String receivedDate;
    private final Result result;

    private MatchResult() {
        receivedDate = DateUtil.format(LocalDateTime.now(ZONE_ID));
        result = null;
    }

    public MatchResult(Result result, LocalDateTime receivedDate) {
        this.receivedDate = DateUtil.format(receivedDate);
        this.result = result;
    }

    public MatchResult(String matchId, Result result) {
        this.receivedDate = DateUtil.format(LocalDateTime.now(ZONE_ID));
        this.result = result;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public Result getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "MatchResult{" +
                "receivedDate=" + receivedDate +
                ", result=" + result +
                '}';
    }
}
