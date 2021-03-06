package hu.bets.points.processor;

import hu.bets.points.model.ProcessingResult;

import java.util.Optional;
import java.util.concurrent.Callable;

public interface ProcessorTask extends Callable<ProcessingResult> {

    Type getType();

    ProcessorTask withPayLoad(Optional<String> payload);
}
