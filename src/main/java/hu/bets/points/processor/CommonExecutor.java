package hu.bets.points.processor;

import hu.bets.points.model.ProcessingResult;

import java.util.Optional;
import java.util.concurrent.CompletionService;

public class CommonExecutor {

    private CompletionService<ProcessingResult> executor;
    private ProcessorTaskFactory taskFactory;

    public CommonExecutor(CompletionService<ProcessingResult> executor, ProcessorTaskFactory taskFactory) {
        this.executor = executor;
        this.taskFactory = taskFactory;
    }

    public void enqueue(Optional<String> payload, Type taskType) {
        executor.submit(taskFactory.taskFor(taskType, payload));
    }
}
