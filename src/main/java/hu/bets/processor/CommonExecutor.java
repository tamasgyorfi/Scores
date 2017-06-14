package hu.bets.processor;

import hu.bets.processor.betbatch.BetBatchTask;
import hu.bets.processor.betbatch.processing.BetBatchProcessor;
import hu.bets.processor.betbatch.validation.BetBatchValidator;
import hu.bets.processor.betrequest.BetRequestTask;
import hu.bets.processor.betrequest.validation.DefaultBetRequestValidator;

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

    public void enqueue(String payload, Type taskType) {
        switch (taskType) {
            case ACKNOWLEDGE_REQUEST: {
                executor.submit(new BetBatchTask(payload, defaultBetBatchValidator, betBatchProcessor));
                break;
            }
            case BETS_REQUEST: {
                executor.submit(new BetRequestTask(payload, new DefaultBetRequestValidator(), null));
                break;
            }
        }
    }
}
