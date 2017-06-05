package hu.bets.points.data;

import hu.bets.model.MatchResult;

public class MatchResultRecord {

    private final MatchResult matchResult;
    private final ProcessingState processingState;

    public MatchResultRecord(final MatchResult matchResult, final ProcessingState processingState) {
        this.matchResult = matchResult;
        this.processingState = processingState;
    }

    public MatchResult getMatchResult() {
        return matchResult;
    }

    public ProcessingState getProcessingState() {
        return processingState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatchResultRecord that = (MatchResultRecord) o;

        if (matchResult != null ? !matchResult.equals(that.matchResult) : that.matchResult != null) return false;
        return processingState == that.processingState;
    }

    @Override
    public int hashCode() {
        int result = matchResult != null ? matchResult.hashCode() : 0;
        result = 31 * result + (processingState != null ? processingState.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MatchResultRecord{" +
                "matchResult=" + matchResult +
                ", processingState=" + processingState +
                '}';
    }
}
