package hu.bets.processor.betrequest.processing;

import com.google.common.collect.Sets;
import hu.bets.model.MatchResult;
import hu.bets.model.SecureMatchResult;
import hu.bets.points.dbaccess.DatabaseException;
import hu.bets.points.dbaccess.ScoresServiceDAO;
import hu.bets.points.services.MatchResultProcessingException;
import hu.bets.points.services.conversion.IllegalJsonException;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Set;

public class DefaultBetRequestProcessor implements BetRequestProcessor {

    private static final Logger LOGGER = Logger.getLogger(DefaultBetRequestProcessor.class);
    private ScoresServiceDAO scoresServiceDAO;

    public DefaultBetRequestProcessor(ScoresServiceDAO scoresServiceDAO) {
        this.scoresServiceDAO = scoresServiceDAO;
    }

    @Override
    public Set<String> process(SecureMatchResult matchResult) {
        try {
            scoresServiceDAO.saveMatch(matchResult.getMatchResult());
            LOGGER.info("MatchResult saved to the database. " + matchResult);

            return Sets.newHashSet(matchResult.getMatchResult().getResult().getMatchId());
        } catch (IllegalJsonException | DatabaseException e) {
            throw new MatchResultProcessingException(e);
        }

    }
}
