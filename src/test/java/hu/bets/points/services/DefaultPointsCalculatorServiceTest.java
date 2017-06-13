package hu.bets.points.services;

import hu.bets.model.Result;
import hu.bets.points.services.points.DefaultPointsCalculatorService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefaultPointsCalculatorServiceTest {

    private DefaultPointsCalculatorService sut = new DefaultPointsCalculatorService();

    @Test
    public void perfectTipShouldResultInTenPointsHomeWins() {
        Result tip = new Result("match1", "competitionId", "1", "2", 1, 0);
        Result result = new Result("match1", "competitionId", "1", "2", 1, 0);

        assertEquals(10, sut.valueTip(tip, result));
    }

    @Test
    public void perfectTipShouldResultInTenPointsAwayWins() {
        Result tip = new Result("match1", "competitionId", "1", "2", 0, 1);
        Result result = new Result("match1", "competitionId", "1", "2", 0, 1);

        assertEquals(10, sut.valueTip(tip, result));
    }

    @Test
    public void perfectTipShouldResultInTenPointsDraw() {
        Result tip = new Result("match1", "competitionId", "1", "2", 1, 1);
        Result result = new Result("match1", "competitionId", "1", "2", 1, 1);

        assertEquals(10, sut.valueTip(tip, result));
    }


    @Test
    public void badTipShouldResultInZeroPointsWhenAwayWins() {
        Result tip = new Result("match1", "competitionId", "1", "2", 1, 0);
        Result result = new Result("match1", "competitionId", "1", "2", 0, 1);

        assertEquals(0, sut.valueTip(tip, result));
    }

    @Test
    public void badTipShouldResultInZeroPointsWhenHomeWins() {
        Result tip = new Result("match1", "competitionId", "1", "2", 0, 1);
        Result result = new Result("match1", "competitionId", "1", "2", 1, 0);

        assertEquals(0, sut.valueTip(tip, result));
    }

    @Test
    public void guessingOutcomeAndOneTeamsGoalsResultsInSevenPointsWhenHomeWins() {
        Result tip = new Result("match1", "competitionId", "1", "3", 2, 1);
        Result result = new Result("match1", "competitionId", "1", "2", 9, 1);

        assertEquals(7, sut.valueTip(tip, result));
    }

    @Test
    public void guessingOutcomeAndOneTeamsGoalsResultsInSevenPointsWhenAwayWins() {
        Result tip = new Result("match1", "competitionId", "1", "3", 1, 2);
        Result result = new Result("match1", "competitionId", "1", "2", 1, 3);

        assertEquals(7, sut.valueTip(tip, result));
    }

    @Test
    public void guessingOutcomeResultsInFivePointsHomeWins() {
        Result tip = new Result("match1", "competitionId", "1", "3", 2, 1);
        Result result = new Result("match1", "competitionId", "1", "2", 9, 0);

        assertEquals(5, sut.valueTip(tip, result));
    }

    @Test
    public void guessingOutcomeResultsInFivePointsAwayWins() {
        Result tip = new Result("match1", "competitionId", "1", "3", 1, 2);
        Result result = new Result("match1", "competitionId", "1", "2", 0, 1);

        assertEquals(5, sut.valueTip(tip, result));
    }

    @Test
    public void guessingOutcomeResultsInFivePointsDraw() {
        Result tip = new Result("match1", "competitionId", "1", "3", 2, 2);
        Result result = new Result("match1", "competitionId", "1", "2", 0, 0);

        assertEquals(5, sut.valueTip(tip, result));
    }


    @Test
    public void guessingAwayTeamsGoalsResultsInTwoPoints() {
        Result tip = new Result("match1", "competitionId", "1", "3", 2, 1);
        Result result = new Result("match1", "competitionId", "1", "2", 0, 1);

        assertEquals(2, sut.valueTip(tip, result));
    }

    @Test
    public void guessingHomeTeamsGoalsResultsInTwoPoints() {
        Result tip = new Result("match1", "competitionId", "1", "3", 2, 1);
        Result result = new Result("match1", "competitionId", "1", "2", 2, 3);

        assertEquals(2, sut.valueTip(tip, result));
    }
}
