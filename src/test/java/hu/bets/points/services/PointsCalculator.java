package hu.bets.points.services;

import hu.bets.points.model.Result;

public class PointsCalculator {

    public int valueTip(Result tip, Result result) {
        if (tip.equals(result)) {
            return 10;
        } else if (outcomeGuessed(tip, result)) {
            if (oneTeamsGoalsGuessed(tip, result)) {
                return 7;
            }
            return 5;
        } else if (oneTeamsGoalsGuessed(tip, result)) {
            return 2;
        }

        return 0;
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
