package hu.bets.messaging.processing;

import hu.bets.messaging.processing.processor.BetBatchProcessor;
import hu.bets.messaging.processing.processor.BetBatchProcessorTask;
import hu.bets.messaging.processing.validation.BetBatchValidator;
import hu.bets.model.ProcessingResult;

import java.util.Set;
import java.util.concurrent.CompletionService;

public class BetsBatchExecutor {

    private CompletionService<ProcessingResult> executor;
    private BetBatchProcessor betBatchProcessor;
    private BetBatchValidator defaultBetBatchValidator;

    public BetsBatchExecutor(CompletionService<ProcessingResult> executor, BetBatchProcessor betBatchProcessor, BetBatchValidator defaultBetBatchValidator) {
        this.executor = executor;
        this.betBatchProcessor = betBatchProcessor;
        this.defaultBetBatchValidator = defaultBetBatchValidator;
    }

    public void enqueue(String payload) {
        executor.submit(new BetBatchProcessorTask(payload, betBatchProcessor, defaultBetBatchValidator));
    }
}
