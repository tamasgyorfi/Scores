package hu.bets.model;

import java.time.LocalDate;
import java.time.ZoneId;

public class MatchResult {

    private final String matchId;
    private final LocalDate matchDate;
    private final Result result;

    public MatchResult(String matchId, Result result) {
        this.matchId = matchId;
        this.matchDate = LocalDate.now(ZoneId.of("UK"));
        this.result = result;
    }

    public String getMatchId() {
        return matchId;
    }

    public LocalDate getMatchDate() {
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
