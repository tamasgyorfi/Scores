package hu.bets.points.services;

import hu.bets.points.dbaccess.ScoresServiceDAO;
import hu.bets.points.model.ToplistEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ToplistServiceImpl implements ToplistService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToplistServiceImpl.class);
    private ScoresServiceDAO scoresServiceDAO;

    public ToplistServiceImpl(ScoresServiceDAO scoresServiceDAO){
        this.scoresServiceDAO = scoresServiceDAO;
    }


    @Override
    public List<ToplistEntry> getToplistScore(List<String> userIds) {
        LOGGER.info("About to call DAO with userIds: {}", userIds);
        Map<String, Long> toplistScores = scoresServiceDAO.getToplistScore(userIds);
        LOGGER.info("DAO returned the following values: {}", toplistScores);

        return userIds.stream().map(elem -> {
            if (toplistScores.containsKey(elem)) {
                return new ToplistEntry(elem, toplistScores.get(elem));
            }

            return new ToplistEntry(elem, 0L);
        }).collect(Collectors.toList());
    }
}
