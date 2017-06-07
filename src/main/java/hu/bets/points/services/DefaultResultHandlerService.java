package hu.bets.points.services;

import hu.bets.model.MatchResult;
import hu.bets.points.dbaccess.DatabaseException;
import hu.bets.points.dbaccess.MatchDAO;
import hu.bets.points.services.conversion.IllegalJsonException;
import hu.bets.points.services.conversion.ModelConverterService;
import org.apache.log4j.Logger;

public class DefaultResultHandlerService implements ResultHandlerService {

    private static final Logger LOGGER = Logger.getLogger(DefaultResultHandlerService.class);

    private final ModelConverterService modelConverterService;
    private final MatchDAO matchDAO;

    public DefaultResultHandlerService(ModelConverterService modelConverterService, MatchDAO matchDAO) {
        this.modelConverterService = modelConverterService;
        this.matchDAO = matchDAO;
    }

    @Override
    public void saveMatchResult(String matchId, String resultRequest) {
        try {
            MatchResult matchResult = modelConverterService.convert(matchId, resultRequest);
            LOGGER.info("MatchResult resulting from conversion: " + matchResult);

            matchDAO.saveMatch(matchResult);
            LOGGER.info("MatchResult saved to the database. " + matchResult);

        } catch (IllegalJsonException | DatabaseException e) {
            throw new MatchResultProcessingException(e);
        }
    }
}
