package hu.bets.points.processor;

import hu.bets.points.processor.betbatch.validation.InvalidBatchException;
import org.apache.log4j.Logger;

import java.util.Set;
import java.util.concurrent.Callable;

public abstract class AbstractProcessorTask<T> implements Callable<ProcessingResult> {

    private static final Logger LOGGER = Logger.getLogger(AbstractProcessorTask.class);

    private String payload;
    private final Validator<T> validator;
    private final Processor<T> processor;

    public AbstractProcessorTask(Validator<T> validator, Processor<T> processor) {
        this.validator = validator;
        this.processor = processor;
    }

    public AbstractProcessorTask(String payload, Validator<T> validator, Processor<T> processor) {
        this.payload = payload;
        this.validator = validator;
        this.processor = processor;
    }

    public AbstractProcessorTask<T> withPayLoad(String payload) {
        this.payload = payload;
        return this;
    }

    @Override
    public ProcessingResult call() {
        try {
            T preProcessedPayload = preProcess();
            validator.validate(preProcessedPayload);
            Set<String> processed = processor.process(preProcessedPayload);
            return new ProcessingResult(processed, getType());
        } catch (InvalidBatchException e) {
            LOGGER.error("Unable to process batch." + e);
        }
        return new ProcessingResult();
    }

    public abstract T preProcess();

    public abstract Type getType();

    public String getPayload() {
        return payload;
    }
}