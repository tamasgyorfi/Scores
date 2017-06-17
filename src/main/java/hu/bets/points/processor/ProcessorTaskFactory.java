package hu.bets.points.processor;

import java.util.Map;
import java.util.Optional;

public class ProcessorTaskFactory {

    private Map<String, ProcessorTask> tasks;

    public ProcessorTaskFactory(Map<String, ProcessorTask> tasks) {
        this.tasks = tasks;
    }

    public ProcessorTask taskFor(Type type, Optional<String> payload) {
        ProcessorTask processorTask = tasks.get(type.name());
        return processorTask.withPayLoad(payload);
    }
}
