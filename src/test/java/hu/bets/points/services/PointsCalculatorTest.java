package hu.bets.points.services;

import hu.bets.points.model.Result;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PointsCalculatorTest {

    private PointsCalculator sut = new PointsCalculator();

    @Test
    public void perfectTipShouldResultInTenPointsHomeWins() {
        Result tip = new Result("1", "2", 1, 0);
        Result result = new Result("1", "2", 1, 0);

        assertEquals(10, sut.valueTip(tip, result));
    }

    @Test
    public void perfectTipShouldResultInTenPointsAwayWins() {
        Result tip = new Result("1", "2", 0, 1);
        Result result = new Result("1", "2", 0, 1);

        assertEquals(10, sut.valueTip(tip, result));
    }

    @Test
    public void perfectTipShouldResultInTenPointsDraw() {
        Result tip = new Result("1", "2", 1, 1);
        Result result = new Result("1", "2", 1, 1);

        assertEquals(10, sut.valueTip(tip, result));
    }


    @Test
    public void badTipShouldResultInZeroPointsWhenAwayWins() {
        Result tip = new Result("1", "2", 1, 0);
        Result result = new Result("1", "2", 0, 1);

        assertEquals(0, sut.valueTip(tip, result));
    }

    @Test
    public void badTipShouldResultInZeroPointsWhenHomeWins() {
        Result tip = new Result("1", "2", 0, 1);
        Result result = new Result("1", "2", 1, 0);

        assertEquals(0, sut.valueTip(tip, result));
    }

    @Test
    public void guessingOutcomeAndOneTeamsGoalsResultsInSevenPointsWhenHomeWins() {
        Result tip = new Result("1", "3", 2, 1);
        Result result = new Result("1", "2", 9, 1);

        assertEquals(7, sut.valueTip(tip, result));
    }

    @Test
    public void guessingOutcomeAndOneTeamsGoalsResultsInSevenPointsWhenAwayWins() {
        Result tip = new Result("1", "3", 1, 2);
        Result result = new Result("1", "2", 1, 3);

        assertEquals(7, sut.valueTip(tip, result));
    }

    @Test
    public void guessingOutcomeResultsInFivePointsHomeWins() {
        Result tip = new Result("1", "3", 2, 1);
        Result result = new Result("1", "2", 9, 0);

        assertEquals(5, sut.valueTip(tip, result));
    }

    @Test
    public void guessingOutcomeResultsInFivePointsAwayWins() {
        Result tip = new Result("1", "3", 1, 2);
        Result result = new Result("1", "2", 0, 1);

        assertEquals(5, sut.valueTip(tip, result));
    }

    @Test
    public void guessingOutcomeResultsInFivePointsDraw() {
        Result tip = new Result("1", "3", 2, 2);
        Result result = new Result("1", "2", 0, 0);

        assertEquals(5, sut.valueTip(tip, result));
    }


    @Test
    public void guessingAwayTeamsGoalsResultsInTwoPoints() {
        Result tip = new Result("1", "3", 2, 1);
        Result result = new Result("1", "2", 0, 1);

        assertEquals(2, sut.valueTip(tip, result));
    }

    @Test
    public void guessingHomeTeamsGoalsResultsInTwoPoints() {
        Result tip = new Result("1", "3", 2, 1);
        Result result = new Result("1", "2", 2, 3);

        assertEquals(2, sut.valueTip(tip, result));
    }

}
