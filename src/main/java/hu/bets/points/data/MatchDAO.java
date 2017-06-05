package hu.bets.points.data;

import hu.bets.model.MatchResult;

public interface MatchDAO {

    /**
     * Saves a match result for future processing.
     *
     * @param matchResult
     */
    void saveMatch(MatchResult matchResult);
}
