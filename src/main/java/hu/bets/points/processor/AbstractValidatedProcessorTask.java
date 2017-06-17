package hu.bets.points.processor;

import hu.bets.points.processor.betbatch.validation.InvalidBatchException;
import org.apache.log4j.Logger;

import java.util.Optional;
import java.util.Set;

public abstract class AbstractValidatedProcessorTask<T> implements ProcessorTask {

    private static final Logger LOGGER = Logger.getLogger(AbstractValidatedProcessorTask.class);

    private String payload;
    private final Validator<T> validator;
    private final Processor<T> processor;

    public AbstractValidatedProcessorTask(Validator<T> validator, Processor<T> processor) {
        this.validator = validator;
        this.processor = processor;
    }

    public AbstractValidatedProcessorTask(String payload, Validator<T> validator, Processor<T> processor) {
        this.payload = payload;
        this.validator = validator;
        this.processor = processor;
    }

    public AbstractValidatedProcessorTask<T> withPayLoad(Optional<String> payload) {
        this.payload = payload.orElseThrow(() -> new IllegalArgumentException(this.getClass() + " cannot be created with empty payload."));
        return this;
    }

    @Override
    public ProcessingResult call() {
        try {
            T preProcessed = preProcess(payload);
            validator.validate(preProcessed);
            Set<String> processed = processor.process(preProcessed);
            return new ProcessingResult(processed, getType());
        } catch (InvalidBatchException e) {
            LOGGER.error("Unable to process batch." + e);
        }
        return new ProcessingResult();
    }

    public abstract T preProcess(String payload);
}
