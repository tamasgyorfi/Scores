package hu.bets.points.services;

import hu.bets.points.model.ToplistEntry;

import java.util.List;

public interface ToplistService {
    /**
     * Returns a list of userId - score mappings for given userIds
     * @param userIds
     * @return a mapping (id-score) for the requested id's
     */
    List<ToplistEntry> getToplistScore(List<String> userIds);
}
