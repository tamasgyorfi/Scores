package hu.bets.points.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchResultWithToken {
    private List<MatchResult> results;
    private String token;

    public MatchResultWithToken() {

    }

    public MatchResultWithToken(List<MatchResult> results, String token) {
        this.results = results;
        this.token = token;
    }

    public List<MatchResult> getResults() {
        return Collections.unmodifiableList(results);
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "MatchResultWithToken{" +
                "results=" + results +
                ", token='" + token + '\'' +
                '}';
    }
}
