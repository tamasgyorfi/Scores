package hu.bets.points.services;

import hu.bets.model.Result;

public interface PointsCalculatorService {

    /**
     * Returns the numeric value associated to a final result guess, given the actual result.
     *
     * @param tip
     * @param result
     * @return
     */
    int valueTip(Result tip, Result result);
}
