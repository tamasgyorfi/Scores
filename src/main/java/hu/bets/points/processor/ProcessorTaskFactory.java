package hu.bets.points.processor;

import java.util.Map;

public class ProcessorTaskFactory {

    private Map<String, AbstractProcessorTask<?>> tasks;

    public ProcessorTaskFactory(Map<String, AbstractProcessorTask<?>> tasks) {
        this.tasks = tasks;
    }

    public AbstractProcessorTask<?> taskFor(Type type, String payload) {
        return tasks.get(type.name()).withPayLoad(payload);
    }
}
