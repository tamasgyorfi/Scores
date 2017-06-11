package hu.bets.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Result {

    private final String competitionId;

    private final String homeTeamId;
    private final String awayTeamId;
    private final int homeTeamGoals;
    private final int awayTeamGoals;

    // Needed for JSON deserialization.
    private Result() {
        awayTeamGoals = -1;
        homeTeamGoals = -1;
        awayTeamId = null;
        homeTeamId = null;
        competitionId = null;
    }

    public Result(String competitionId, String homeTeamId, String awayTeamId, int homeTeamGoals, int awayTeamGoals) {
        this.competitionId = competitionId;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
        this.homeTeamGoals = homeTeamGoals;
        this.awayTeamGoals = awayTeamGoals;
    }

    public String getHomeTeamId() {
        return homeTeamId;
    }

    public String getAwayTeamId() {
        return awayTeamId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result = (Result) o;

        if (homeTeamGoals != result.homeTeamGoals) return false;
        if (awayTeamGoals != result.awayTeamGoals) return false;
        if (competitionId != null ? !competitionId.equals(result.competitionId) : result.competitionId != null)
            return false;
        if (homeTeamId != null ? !homeTeamId.equals(result.homeTeamId) : result.homeTeamId != null) return false;
        return awayTeamId != null ? awayTeamId.equals(result.awayTeamId) : result.awayTeamId == null;
    }

    @Override
    public int hashCode() {
        int result = competitionId != null ? competitionId.hashCode() : 0;
        result = 31 * result + (homeTeamId != null ? homeTeamId.hashCode() : 0);
        result = 31 * result + (awayTeamId != null ? awayTeamId.hashCode() : 0);
        result = 31 * result + homeTeamGoals;
        result = 31 * result + awayTeamGoals;
        return result;
    }

    @Override
    public String toString() {
        return "Result{" +
                "competitionId='" + competitionId + '\'' +
                ", homeTeamId='" + homeTeamId + '\'' +
                ", awayTeamId='" + awayTeamId + '\'' +
                ", homeTeamGoals=" + homeTeamGoals +
                ", awayTeamGoals=" + awayTeamGoals +
                '}';
    }
}
