package hu.bets.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UnprocessedMatch {
    private String matchId;

    private UnprocessedMatch() {
    }

    public UnprocessedMatch(String matchId) {
        this.matchId = matchId;
    }

    public String getMatchId() {
        return matchId;
    }
}
