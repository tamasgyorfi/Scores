package hu.bets.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Bet {

    private String matchId;
    private String betId;
    private String userId;

    @JsonUnwrapped
    private Result result;

    private Bet() {

    }

    public Bet(String userId, String matchId, Result result, String betId) {
        this.userId = userId;
        this.matchId = matchId;
        this.result = result;
        this.betId = betId;
    }

    public Result getResult() {
        return result;
    }

    public String getUserId() {
        return userId;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getBetId() {
        return betId;
    }
}
