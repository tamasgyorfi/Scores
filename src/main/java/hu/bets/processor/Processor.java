package hu.bets.processor;

import java.util.Set;

public interface Processor<T> {

    Set<String> process(T toProcess);
}
