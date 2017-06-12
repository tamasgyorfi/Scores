package hu.bets.messaging.processing;

import com.google.common.collect.Sets;
import hu.bets.common.util.hash.HashGenerator;
import hu.bets.messaging.processing.processor.BetBatchProcessorTask;
import hu.bets.messaging.processing.processor.DefaultBetBatchProcessor;
import hu.bets.messaging.processing.validation.DefaultBetBatchValidator;
import hu.bets.model.Bet;
import hu.bets.model.BetsBatch;
import hu.bets.model.ProcessingResult;
import hu.bets.model.Result;
import hu.bets.points.dbaccess.ScoresServiceDAO;
import hu.bets.points.services.points.PointsCalculatorService;
import hu.bets.utils.JsonUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BetBatchProcessorTaskTest {

    private static final String FAKE_PAYLOAD = "Fake";
    private static final String HASH = "HASH";
    private BetBatchProcessorTask sut;

    @Mock
    private JsonUtils jsonUtils;
    @Mock
    private PointsCalculatorService pointsCalculatorService;
    @Mock
    private ScoresServiceDAO dataAccess;
    @Mock
    private HashGenerator hashGenerator;

    @Before
    public void setup() {
        sut = new FakeBetBatchProcessorTask(FAKE_PAYLOAD, pointsCalculatorService, dataAccess, hashGenerator);
    }

    @Test
    public void shouldDoNothingIfThePayloadIsNotParsable() throws Exception {
        when(jsonUtils.fromJson(FAKE_PAYLOAD, BetsBatch.class)).thenThrow(new IllegalArgumentException("aa"));

        ProcessingResult<Set<String>> result = sut.call();
        assertEquals(null, result.getPayload());
    }

    @Test
    public void shouldDoNothingWhenHashCheckFails() throws Exception {
        List<Bet> bets = new ArrayList<>();
        bets.add(getBet("1", "99"));
        BetsBatch betsBatch = new BetsBatch(1, bets, HASH);

        when(jsonUtils.fromJson(FAKE_PAYLOAD, BetsBatch.class)).thenReturn(betsBatch);
        when(hashGenerator.getHash(bets)).thenReturn("different hash");

        ProcessingResult<Set<String>> result = sut.call();
        assertEquals(null, result.getPayload());
    }

    @Test
    public void shouldDoNothingWhenArityCheckFails() throws Exception {
        List<Bet> bets = new ArrayList<>();
        bets.add(getBet("1", "99"));
        BetsBatch betsBatch = new BetsBatch(2, bets, HASH);

        when(jsonUtils.fromJson(FAKE_PAYLOAD, BetsBatch.class)).thenReturn(betsBatch);
        when(hashGenerator.getHash(bets)).thenReturn(HASH);

        ProcessingResult<Set<String>> result = sut.call();
        assertEquals(null, result.getPayload());
    }

    @Test
    public void shouldReturnFourIdsForFourFailedMatches() throws Exception {
        List<Bet> bets = new ArrayList<>();
        bets.add(getBet("1", "99"));
        bets.add(getBet("2", "98"));
        bets.add(getBet("3", "99"));
        bets.add(getBet("4", "96"));
        bets.add(getBet("5", "95"));
        BetsBatch betsBatch = new BetsBatch(5, bets, HASH);

        Result result1 = getResult();
        Result result2 = getResult();
        Result result3 = getResult();

        when(jsonUtils.fromJson(FAKE_PAYLOAD, BetsBatch.class)).thenReturn(betsBatch);
        when(hashGenerator.getHash(bets)).thenReturn(HASH);

        when(dataAccess.getResult("1")).thenReturn(Optional.empty());
        when(dataAccess.getResult("2")).thenThrow(new IllegalArgumentException());
        when(dataAccess.getResult("3")).thenReturn(Optional.of(result1));
        when(dataAccess.getResult("4")).thenReturn(Optional.of(result2));
        when(dataAccess.getResult("5")).thenReturn(Optional.of(result3));

        when(pointsCalculatorService.valueTip(bets.get(2).getResult(), result1)).thenThrow(new NullPointerException());
        when(pointsCalculatorService.valueTip(bets.get(3).getResult(), result2)).thenReturn(10);
        when(pointsCalculatorService.valueTip(bets.get(4).getResult(), result3)).thenReturn(7);

        doThrow(new IllegalArgumentException()).when(dataAccess).savePoints(bets.get(3), 10);

        ProcessingResult<Set<String>> result = sut.call();


        assertEquals(1, result.getPayload().size());
        assertEquals(Sets.newHashSet("95"), result.getPayload());
        verify(dataAccess).saveNonProcessedMatches(Sets.newHashSet("1", "2", "3", "4"));
    }

    private Result getResult() {
        return new Result(UUID.randomUUID().toString(), "1", "2", 3, 0);
    }


    private Bet getBet(String matchId, String betId) {
        return new Bet("userId", matchId, new Result("competitionId", "1", "2", 3, 2), betId);
    }

    private class FakeBetBatchProcessorTask extends BetBatchProcessorTask {

        public FakeBetBatchProcessorTask(String batchPayload, PointsCalculatorService pointsCalculatorService, ScoresServiceDAO dataAccess, HashGenerator hashGenerator) {
            super(batchPayload, new DefaultBetBatchProcessor(dataAccess, pointsCalculatorService), new DefaultBetBatchValidator(hashGenerator));
        }

        @Override
        protected JsonUtils getMapper() {
            return jsonUtils;
        }
    }
}