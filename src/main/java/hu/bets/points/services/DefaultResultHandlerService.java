package hu.bets.points.services;

import hu.bets.model.FinalMatchResult;
import hu.bets.points.dbaccess.DatabaseException;
import hu.bets.points.dbaccess.ScoresServiceDAO;
import hu.bets.points.services.conversion.IllegalJsonException;
import hu.bets.points.services.conversion.ModelConverterService;
import org.apache.log4j.Logger;

public class DefaultResultHandlerService implements ResultHandlerService {

    private static final Logger LOGGER = Logger.getLogger(DefaultResultHandlerService.class);

    private final ModelConverterService modelConverterService;
    private final ScoresServiceDAO scoresServiceDAO;

    public DefaultResultHandlerService(ModelConverterService modelConverterService, ScoresServiceDAO scoresServiceDAO) {
        this.modelConverterService = modelConverterService;
        this.scoresServiceDAO = scoresServiceDAO;
    }

    @Override
    public void saveMatchResult(String matchId, String resultRequest) {
        try {
            FinalMatchResult finalMatchResult = modelConverterService.convert(matchId, resultRequest);
            LOGGER.info("FinalMatchResult resulting from conversion: " + finalMatchResult);

            scoresServiceDAO.saveMatch(finalMatchResult);
            LOGGER.info("FinalMatchResult saved to the database. " + finalMatchResult);

        } catch (IllegalJsonException | DatabaseException e) {
            throw new MatchResultProcessingException(e);
        }
    }
}
