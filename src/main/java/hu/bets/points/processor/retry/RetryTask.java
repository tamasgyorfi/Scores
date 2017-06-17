package hu.bets.points.processor.retry;

import hu.bets.points.dbaccess.ScoresServiceDAO;
import hu.bets.points.processor.ProcessingResult;
import hu.bets.points.processor.ProcessorTask;
import hu.bets.points.processor.Type;
import org.apache.log4j.Logger;

import java.util.Optional;
import java.util.Set;

public class RetryTask implements ProcessorTask {

    private static final Logger LOGGER = Logger.getLogger(RetryTask.class);

    private ScoresServiceDAO dataAccess;

    public RetryTask(ScoresServiceDAO dataAccess) {
        this.dataAccess = dataAccess;
    }

    @Override
    public ProcessingResult call() {
        Set<String> failedMatchIds = dataAccess.getFailedMatchIds();
        LOGGER.info("Re-requesting match IDs for the following matches: " + failedMatchIds);
        return new ProcessingResult(failedMatchIds, getType());
    }

    @Override
    public Type getType() {
        return Type.RETRY_REQUEST;
    }

    @Override
    public ProcessorTask withPayLoad(Optional<String> payload) {
        return this;
    }
}
