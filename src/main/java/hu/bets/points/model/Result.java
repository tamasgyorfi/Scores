package hu.bets.points.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {

    private final String matchId;
    private final String competitionId;

    private final String homeTeamName;
    private final String awayTeamName;
    private final int homeTeamGoals;
    private final int awayTeamGoals;

    private final String matchDate;

    // Needed for JSON deserialization.
    private Result() {
        matchId = null;
        awayTeamGoals = -1;
        homeTeamGoals = -1;
        awayTeamName = null;
        homeTeamName = null;
        competitionId = null;
        matchDate = null;
    }

    public Result(String matchId, String competitionId, String homeTeamName, String awayTeamName, int homeTeamGoals, int awayTeamGoals, String matchDate) {
        this.matchId = matchId;
        this.competitionId = competitionId;
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.homeTeamGoals = homeTeamGoals;
        this.awayTeamGoals = awayTeamGoals;
        this.matchDate = matchDate;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public int getHomeTeamGoals() {
        return homeTeamGoals;
    }

    public int getAwayTeamGoals() {
        return awayTeamGoals;
    }

    public String getCompetitionId() {
        return competitionId;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getMatchDate() {
        return matchDate;
    }

    @Override
    public String toString() {
        return "Result{" +
                "matchId='" + matchId + '\'' +
                ", competitionId='" + competitionId + '\'' +
                ", homeTeamName='" + homeTeamName + '\'' +
                ", awayTeamName='" + awayTeamName + '\'' +
                ", homeTeamGoals=" + homeTeamGoals +
                ", awayTeamGoals=" + awayTeamGoals +
                ", matchDate='" + matchDate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return homeTeamGoals == result.homeTeamGoals &&
                awayTeamGoals == result.awayTeamGoals &&
                Objects.equals(matchId, result.matchId) &&
                Objects.equals(competitionId, result.competitionId) &&
                Objects.equals(homeTeamName, result.homeTeamName) &&
                Objects.equals(awayTeamName, result.awayTeamName) &&
                Objects.equals(matchDate, result.matchDate);
    }

    @Override
    public int hashCode() {

        return Objects.hash(matchId, competitionId, homeTeamName, awayTeamName, homeTeamGoals, awayTeamGoals, matchDate);
    }
}
