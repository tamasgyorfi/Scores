package hu.bets.processor;

import hu.bets.processor.betbatch.processing.BetBatchProcessor;
import hu.bets.processor.betbatch.BetBatchTask;
import hu.bets.processor.betbatch.validation.BetBatchValidator;

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
        executor.submit(new BetBatchTask(payload, defaultBetBatchValidator, betBatchProcessor));
    }
}
