package hu.bets.points.dbaccess;

import hu.bets.points.model.Bet;
import hu.bets.points.model.MatchResult;
import hu.bets.points.model.Result;

import java.util.Optional;
import java.util.Set;

public interface ScoresServiceDAO {

    /**
     * Saves a match result for future processing.
     *
     * @param matchResult
     */
    void saveMatch(MatchResult matchResult);

    /**
     * Returns all the matches which have unprocessed bets.
     *
     * @return the match IDs with unprocessed bet(s).
     */
    Set<String> getFailedMatchIds();

    /**
     * Saves the value associated with this bet into the database
     *
     * @param bet
     * @param value
     */
    void savePoints(Bet bet, int value);

    /**
     * Give a matchId, finds the final result of that match, if present, or empty otherwise.
     * Reasons while this method could return {@link Optional}.empty():
     *      - the match has not finished yet
     *      - the matchId is not associated with any matches.
     *
     * @param matchId
     * @return
     */
    Optional<Result> getResult(String matchId);

    /**
     * Saves matchIDs for which there are unprocessed bets.
     *
     * @param unprocessedMatches
     */
    void saveNonProcessedMatches(Set<String> unprocessedMatches);
}
