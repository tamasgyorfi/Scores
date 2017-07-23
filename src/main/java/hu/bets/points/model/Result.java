package hu.bets.points.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {

    private final String matchId;
    private final String competitionId;

    private final String homeTeamName;
    private final String awayTeamName;
    private final int homeTeamGoals;
    private final int awayTeamGoals;

    // Needed for JSON deserialization.
    private Result() {
        matchId = null;
        awayTeamGoals = -1;
        homeTeamGoals = -1;
        awayTeamName = null;
        homeTeamName = null;
        competitionId = null;
    }

    public Result(String matchId, String competitionId, String homeTeamName, String awayTeamName, int homeTeamGoals, int awayTeamGoals) {
        this.matchId = matchId;
        this.competitionId = competitionId;
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.homeTeamGoals = homeTeamGoals;
        this.awayTeamGoals = awayTeamGoals;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result = (Result) o;

        if (homeTeamGoals != result.homeTeamGoals) return false;
        if (awayTeamGoals != result.awayTeamGoals) return false;
        if (matchId != null ? !matchId.equals(result.matchId) : result.matchId != null) return false;
        if (competitionId != null ? !competitionId.equals(result.competitionId) : result.competitionId != null)
            return false;
        if (homeTeamName != null ? !homeTeamName.equals(result.homeTeamName) : result.homeTeamName != null) return false;
        return awayTeamName != null ? awayTeamName.equals(result.awayTeamName) : result.awayTeamName == null;
    }

    @Override
    public int hashCode() {
        int result = matchId != null ? matchId.hashCode() : 0;
        result = 31 * result + (competitionId != null ? competitionId.hashCode() : 0);
        result = 31 * result + (homeTeamName != null ? homeTeamName.hashCode() : 0);
        result = 31 * result + (awayTeamName != null ? awayTeamName.hashCode() : 0);
        result = 31 * result + homeTeamGoals;
        result = 31 * result + awayTeamGoals;
        return result;
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
                '}';
    }
}
