package hu.bets.points.dbaccess;

import hu.bets.model.Bet;
import hu.bets.model.FinalMatchResult;
import hu.bets.model.Result;

import java.util.Collection;
import java.util.Optional;

public interface ScoresServiceDAO {

    /**
     * Saves a match result for future processing.
     *
     * @param finalMatchResult
     */
    void saveMatch(FinalMatchResult finalMatchResult);

    /**
     * Marks the match identified by matchId as incomplete for bets processing.
     *
     * @param matchId
     */
    void betProcessingFailedFor(String matchId);

    /**
     * Returns all the matches which have unprocessed bets.
     *
     * @return the match IDs with unprocessed bet(s).
     */
    Collection<String> getFailedMatchIds();

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
}
