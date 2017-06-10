package hu.bets.model;

import java.util.Collections;
import java.util.Set;

public class ProcessingResult {

    private Set<String> failedMatchIds;
    private Set<String> betIdsToAcknowledge;

    public ProcessingResult(Set<String> failedMatchIds, Set<String> betIdsToAcknowledge) {
        this.failedMatchIds = failedMatchIds;
        this.betIdsToAcknowledge = betIdsToAcknowledge;
    }

    public Set<String> getFailedMatchIds() {
        return Collections.unmodifiableSet(failedMatchIds);
    }

    public Set<String> getBetIdsToAcknowledge() {
        return Collections.unmodifiableSet(betIdsToAcknowledge);
    }
}
