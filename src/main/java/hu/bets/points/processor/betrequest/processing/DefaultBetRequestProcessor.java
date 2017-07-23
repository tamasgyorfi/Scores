package hu.bets.points.processor.betrequest.processing;

import hu.bets.points.dbaccess.DatabaseException;
import hu.bets.points.dbaccess.ScoresServiceDAO;
import hu.bets.points.model.MatchResult;
import hu.bets.points.model.MatchResultWithToken;
import hu.bets.points.services.MatchResultProcessingException;
import hu.bets.points.services.conversion.IllegalJsonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class DefaultBetRequestProcessor implements BetRequestProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultBetRequestProcessor.class);
    private ScoresServiceDAO scoresServiceDAO;

    public DefaultBetRequestProcessor(ScoresServiceDAO scoresServiceDAO) {
        this.scoresServiceDAO = scoresServiceDAO;
    }

    @Override
    public Set<String> process(MatchResultWithToken matchResults) {
        Set<String> retVal = new HashSet<>();

        for (MatchResult matchResult : matchResults.getResults()) {
            try {
                scoresServiceDAO.saveMatch(matchResult);
                LOGGER.info("MatchResult saved to the database. " + matchResult);
                retVal.add(matchResult.getResult().getMatchId());
            } catch (IllegalJsonException | DatabaseException e) {
                throw new MatchResultProcessingException(e);
            }

        }
        return retVal;
    }
}
