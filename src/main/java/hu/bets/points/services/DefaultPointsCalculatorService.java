package hu.bets.points.services;

import hu.bets.model.Result;

public class DefaultPointsCalculatorService implements PointsCalculatorService {

    private static final int PERFECT_GUESS_POINTS = 10;
    private static final int OUTCOME_GUESSED_POINTS = 5;
    private static final int ONE_TEAM_SCORE_GUESSED_POINTS = 2;

    public int valueTip(Result tip, Result result) {
        if (tip.equals(result)) {
            return PERFECT_GUESS_POINTS;
        }

        int points = 0;

        if (outcomeGuessed(tip, result)) {
            points += OUTCOME_GUESSED_POINTS;
        }
        if (oneTeamsGoalsGuessed(tip, result)) {
            points += ONE_TEAM_SCORE_GUESSED_POINTS;
        }

        return points;
    }

    private boolean oneTeamsGoalsGuessed(Result tip, Result result) {

        return result.getAwayTeamGoals() == tip.getAwayTeamGoals() ||
                result.getHomeTeamGoals() == tip.getHomeTeamGoals();
    }

    private boolean outcomeGuessed(Result tip, Result result) {
        int tipGoalDifference = tip.getHomeTeamGoals() - tip.getAwayTeamGoals();
        int resultGoalDifference = result.getHomeTeamGoals() - result.getAwayTeamGoals();

        return (tipGoalDifference == 0 && resultGoalDifference == 0) ||
                (tipGoalDifference < 0 && resultGoalDifference < 0) ||
                (tipGoalDifference > 0 && resultGoalDifference > 0);
    }
}
