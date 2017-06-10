package hu.bets.messaging.processing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import hu.bets.common.util.HashGenerator;
import hu.bets.messaging.processing.processor.DefaultBetBatchProcessor;
import hu.bets.messaging.processing.validation.DefaultBetBatchValidator;
import hu.bets.model.Bet;
import hu.bets.model.BetsBatch;
import hu.bets.model.ProcessingResult;
import hu.bets.model.Result;
import hu.bets.points.dbaccess.ScoresServiceDAO;
import hu.bets.points.services.points.PointsCalculatorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BetBatchProcessorTaskTest {

    private static final String FAKE_PAYLOAD = "Fake";
    private static final String HASH = "HASH";
    private BetBatchProcessorTask sut;

    @Mock
    private ObjectMapper objectMapper;
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
        when(objectMapper.readValue(FAKE_PAYLOAD, BetsBatch.class)).thenThrow(new IllegalArgumentException("aa"));

        ProcessingResult result = sut.call();
        assertEquals(0, result.getBetIdsToAcknowledge().size());
        assertEquals(0, result.getFailedMatchIds().size());
    }

    @Test
    public void shouldDoNothingWhenHashCheckFails() throws Exception {
        List<Bet> bets = new ArrayList<>();
        bets.add(getBet("1", "99"));
        BetsBatch betsBatch = new BetsBatch(1, bets, HASH);

        when(objectMapper.readValue(FAKE_PAYLOAD, BetsBatch.class)).thenReturn(betsBatch);
        when(hashGenerator.getHash(bets)).thenReturn("different hash");

        ProcessingResult result = sut.call();
        assertEquals(0, result.getBetIdsToAcknowledge().size());
        assertEquals(0, result.getFailedMatchIds().size());
    }

    @Test
    public void shouldDoNothingWhenArityCheckFails() throws Exception {
        List<Bet> bets = new ArrayList<>();
        bets.add(getBet("1", "99"));
        BetsBatch betsBatch = new BetsBatch(2, bets, HASH);

        when(objectMapper.readValue(FAKE_PAYLOAD, BetsBatch.class)).thenReturn(betsBatch);
        when(hashGenerator.getHash(bets)).thenReturn(HASH);

        ProcessingResult result = sut.call();
        assertEquals(0, result.getBetIdsToAcknowledge().size());
        assertEquals(0, result.getFailedMatchIds().size());
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

        when(objectMapper.readValue(FAKE_PAYLOAD, BetsBatch.class)).thenReturn(betsBatch);
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

        ProcessingResult result = sut.call();

        assertEquals(4, result.getFailedMatchIds().size());
        assertEquals(Sets.newHashSet("1", "2", "3", "4"), result.getFailedMatchIds());

        assertEquals(1, result.getBetIdsToAcknowledge().size());
        assertEquals(Sets.newHashSet("95"), result.getBetIdsToAcknowledge());
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
        protected ObjectMapper getMapper() {
            return objectMapper;
        }
    }
}