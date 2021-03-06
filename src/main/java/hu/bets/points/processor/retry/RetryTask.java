package hu.bets.points.processor.retry;

import hu.bets.points.dbaccess.ScoresServiceDAO;
import hu.bets.points.model.ProcessingResult;
import hu.bets.points.processor.ProcessorTask;
import hu.bets.points.processor.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;

public class RetryTask implements ProcessorTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryTask.class);

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
