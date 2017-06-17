package hu.bets.points.processor.retry;

import hu.bets.points.processor.CommonExecutor;
import hu.bets.points.processor.Type;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RetryTaskRunner {

    private static final Logger LOGGER = Logger.getLogger(RetryTaskRunner.class);

    @Value("${retry.interval.seconds:3600}")
    int retryInterval;

    private CommonExecutor commonExecutor;
    private ScheduledExecutorService executorService;

    public RetryTaskRunner(CommonExecutor commonExecutor, ScheduledExecutorService executorService) {
        this.commonExecutor = commonExecutor;
        this.executorService = executorService;
    }

    public void run() {
        LOGGER.info("Retry thread successfully started.");
        executorService.scheduleAtFixedRate(() -> commonExecutor.enqueue(Optional.empty(), Type.RETRY_REQUEST), retryInterval, retryInterval, TimeUnit.SECONDS);
    }

    public void kill() {
        executorService.shutdownNow();
    }
}
