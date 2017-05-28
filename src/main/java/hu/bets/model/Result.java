package hu.bets.model;

public class Result {

    private final String homeTeamId;
    private final String awayTeamId;
    private final int homeTeamGoals;
    private final int awayTeamGoals;

    public Result(String homeTeamId, String awayTeamId, int homeTeamGoals, int awayTeamGoals) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result = (Result) o;

        if (homeTeamGoals != result.homeTeamGoals) return false;
        if (awayTeamGoals != result.awayTeamGoals) return false;
        if (homeTeamId != null ? !homeTeamId.equals(result.homeTeamId) : result.homeTeamId != null) return false;
        return awayTeamId != null ? awayTeamId.equals(result.awayTeamId) : result.awayTeamId == null;
    }

    @Override
    public int hashCode() {
        int result = homeTeamId != null ? homeTeamId.hashCode() : 0;
        result = 31 * result + (awayTeamId != null ? awayTeamId.hashCode() : 0);
        result = 31 * result + homeTeamGoals;
        result = 31 * result + awayTeamGoals;
        return result;
    }
}
