package org.powertac.rachma.game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class GameTests {

    @Test
    void latestSuccessfulRunIsNullTest() {
        Game game = new Game();
        game.setRuns(generateRuns(1, 4, 0));
        Assertions.assertNull(game.getLatestSuccessfulRun());
    }

    @Test
    void latestSuccessfulRunTest() {
        Game game = new Game();
        game.setRuns(generateRuns(1, 3, 1));
        Assertions.assertTrue(game.getLatestSuccessfulRun().wasSuccessful());
    }

    @Test
    void latestSuccessfulRunWithSeveralSuccessfulRunsTest() {
        Game game = new Game();
        List<GameRun> runs = generateRuns(2, 8, 5);
        game.setRuns(runs);
        Assertions.assertTrue(game.getLatestSuccessfulRun().wasSuccessful());
        Assertions.assertEquals(
            runs.stream()
                .filter(GameRun::wasSuccessful)
                .map(GameRun::getEnd)
                .max(Instant::compareTo).get(),
            game.getLatestSuccessfulRun().getEnd());
    }

    private List<GameRun> generateRuns(int running, int failed, int successful) {
        List<GameRun> runs = new ArrayList<>();
        for (int i = 0; i < running; i++) {
            GameRun run = Mockito.mock(GameRun.class);
            Mockito.when(run.wasSuccessful()).thenReturn(false);
            Mockito.when(run.getEnd()).thenReturn(null);
            runs.add(run);
        }
        for (int i = 0; i < failed; i++) {
            GameRun run = Mockito.mock(GameRun.class);
            Mockito.when(run.wasSuccessful()).thenReturn(false);
            Mockito.when(run.getEnd()).thenReturn(Instant.now()); // TODO : how to better mock dates & date ranges
            runs.add(run);
        }
        for (int i = 0; i < successful; i++) {
            GameRun run = Mockito.mock(GameRun.class);
            Mockito.when(run.wasSuccessful()).thenReturn(true);
            Mockito.when(run.getEnd()).thenReturn(Instant.now());
            runs.add(run);
        }
        return runs;
    }

}
