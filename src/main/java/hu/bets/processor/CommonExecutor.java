package hu.bets.processor;

import java.util.concurrent.CompletionService;

public class CommonExecutor {

    private CompletionService<ProcessingResult> executor;
    private ProcessorTaskFactory taskFactory;

    public CommonExecutor(CompletionService<ProcessingResult> executor, ProcessorTaskFactory taskFactory) {
        this.executor = executor;
        this.taskFactory = taskFactory;
    }

    public void enqueue(String payload, Type taskType) {
        executor.submit(taskFactory.taskFor(taskType, payload));
    }
}
