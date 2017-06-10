package hu.bets.messaging.processing.processor;

import hu.bets.model.Bet;
import hu.bets.model.BetsBatch;
import hu.bets.model.ProcessingResult;
import hu.bets.model.Result;
import hu.bets.points.dbaccess.ScoresServiceDAO;
import hu.bets.points.services.points.PointsCalculatorService;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DefaultBetBatchProcessor implements BetBatchProcessor {

    private static final Logger LOGGER = Logger.getLogger(DefaultBetBatchProcessor.class);
    private final ScoresServiceDAO dataAccess;
    private final PointsCalculatorService pointsCalculatorService;

    public DefaultBetBatchProcessor(ScoresServiceDAO dataAccess, PointsCalculatorService pointsCalculatorService) {
        this.dataAccess = dataAccess;
        this.pointsCalculatorService = pointsCalculatorService;
    }

    public ProcessingResult processMatches(BetsBatch betsBatch) {

        Set<String> unprocessedMatches = new HashSet<>();
        Set<String> processedBets = new HashSet<>();

        for (Bet bet : betsBatch.getBets()) {
            try {
                processOneMatch(bet);
                processedBets.add(bet.getBetId());
            } catch (BatchProcessingException e) {
                LOGGER.error("Error processing bet with id " + bet.getBetId(), e);
                unprocessedMatches.add(bet.getMatchId());
            }
        }

        return new ProcessingResult(unprocessedMatches, processedBets);
    }

    private void processOneMatch(Bet bet) {
        try {
            Optional<Result> result = dataAccess.getResult(bet.getMatchId());

            int value = pointsCalculatorService.valueTip(bet.getResult(), result.orElseThrow(() -> new BatchProcessingException("Unable to fid result for match ID: " + bet.getMatchId())));
            dataAccess.savePoints(bet, value);
        } catch (Exception e) {
            throw new BatchProcessingException(e);
        }
    }

}
