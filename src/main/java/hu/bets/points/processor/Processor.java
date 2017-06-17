package hu.bets.points.processor;

import java.util.Set;

public interface Processor<T> {

    /**
     * Processes a generic payload and returns the IDs that represent the outcome of the processing.
     *
     * @param toProcess
     * @return the IDs the processing results in.
     */
    Set<String> process(T toProcess);
}
