package hu.bets.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SecureMatchResult {
    private MatchResult matchResult;
    private String securityToken;

    public SecureMatchResult() {

    }

    public SecureMatchResult(MatchResult matchResult, String securityToken) {
        this.matchResult = matchResult;
        this.securityToken = securityToken;
    }

    public MatchResult getMatchResult() {
        return matchResult;
    }

    public String getSecurityToken() {
        return securityToken;
    }
}
