package hu.bets.points.dbaccess;

import hu.bets.model.MatchResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public interface MatchDAO {

    /**
     * Saves a match result for future processing.
     *
     * @param matchResult
     */
    void saveMatch(MatchResult matchResult);

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
}
