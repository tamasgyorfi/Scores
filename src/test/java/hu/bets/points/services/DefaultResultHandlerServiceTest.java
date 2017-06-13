package hu.bets.points.services;

import hu.bets.model.MatchResult;
import hu.bets.points.dbaccess.ScoresServiceDAO;
import hu.bets.points.services.conversion.ModelConverterService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DefaultResultHandlerServiceTest {

    @Mock
    private ModelConverterService modelConverterService;
    @Mock
    private ScoresServiceDAO scoresServiceDAO;

    private DefaultResultHandlerService sut;

    @Before
    public void setup() {
        sut = new DefaultResultHandlerService(modelConverterService, scoresServiceDAO);
    }

    @Test
    public void saveResultShouldConvertThePayloadAndSaveToTheDatabase() {

        MatchResult matchResult = Mockito.mock(MatchResult.class);

        Mockito.when(modelConverterService.convert("matchId1", "")).thenReturn(matchResult);
        sut.saveMatchResult("matchId1", "");

        Mockito.verify(scoresServiceDAO).saveMatch(matchResult);
    }
}
