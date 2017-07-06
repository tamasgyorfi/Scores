package hu.bets.points.services;

import hu.bets.points.dbaccess.DatabaseException;
import hu.bets.points.dbaccess.ScoresServiceDAO;
import hu.bets.points.model.SecureMatchResult;
import hu.bets.points.services.conversion.IllegalJsonException;
import hu.bets.points.services.conversion.ModelConverterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultResultHandlerService implements ResultHandlerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultResultHandlerService.class);

    private final ModelConverterService modelConverterService;
    private final ScoresServiceDAO scoresServiceDAO;

    public DefaultResultHandlerService(ModelConverterService modelConverterService, ScoresServiceDAO scoresServiceDAO) {
        this.modelConverterService = modelConverterService;
        this.scoresServiceDAO = scoresServiceDAO;
    }

    @Override
    public void saveMatchResult(String matchId, String resultRequest) {
        try {
            SecureMatchResult matchResult = modelConverterService.convert(matchId, resultRequest);
            LOGGER.info("MatchResult resulting from conversion: " + matchResult);

            scoresServiceDAO.saveMatch(matchResult.getMatchResult());
            LOGGER.info("MatchResult saved to the database. " + matchResult);

        } catch (IllegalJsonException | DatabaseException e) {
            throw new MatchResultProcessingException(e);
        }
    }
}
