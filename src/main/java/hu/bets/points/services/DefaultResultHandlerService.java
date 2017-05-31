package hu.bets.points.services;

import hu.bets.model.MatchResult;
import hu.bets.points.data.MatchDAO;
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
    public void saveResult(String matchId, String resultRequest) {
        MatchResult matchResult = modelConverterService.convert(matchId, resultRequest);
        LOGGER.info("MatchResult resulting from conversion: " + matchResult);
    }
}
