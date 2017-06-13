package hu.bets.processor;

import hu.bets.processor.betprocessing.BetBatchProcessor;
import hu.bets.processor.betprocessing.BetBatchProcessorTask;
import hu.bets.processor.betprocessing.validation.BetBatchValidator;

import java.util.concurrent.CompletionService;

public class CommonExecutor {

    private CompletionService<ProcessingResult> executor;
    private BetBatchProcessor betBatchProcessor;
    private BetBatchValidator defaultBetBatchValidator;

    public CommonExecutor(CompletionService<ProcessingResult> executor, BetBatchProcessor betBatchProcessor, BetBatchValidator defaultBetBatchValidator) {
        this.executor = executor;
        this.betBatchProcessor = betBatchProcessor;
        this.defaultBetBatchValidator = defaultBetBatchValidator;
    }

    public void enqueue(String payload) {
        executor.submit(new BetBatchProcessorTask(payload, betBatchProcessor, defaultBetBatchValidator));
    }
}
