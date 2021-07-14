package org.powertac.rachma.game;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Component
public class LimitedCapacityGameScheduler implements GameScheduler {

    private final static int maximumCapacity = 1;

    private final GameRepository games;
    private final GameRunner runner;

    private final ExecutorService gamePool;
    private final Map<Game, Future<Game>> running;

    public LimitedCapacityGameScheduler(GameRepository games, GameRunner runner) {
        this.games = games;
        this.runner = runner;
        gamePool = Executors.newFixedThreadPool(maximumCapacity);
        running = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized void runGames() {
        updateRunningGames();
        if(hasCapacity()) {
            runNext();
        }
    }

    private void updateRunningGames() {
        for (Map.Entry<Game, Future<Game>> run : running.entrySet()) {
            if (run.getValue().isDone()) {
                running.remove(run.getKey());
            }
        }
    }

    private boolean hasCapacity() {
        return running.size() < maximumCapacity;
    }

    private void runNext() {
        Game game = games.findOneQueued();
        if (null != game) {
            running.put(game, gamePool.submit(createRun(game)));
        }
    }

    private Callable<Game> createRun(Game game) {
        return () -> {
            runner.run(game);
            return game;
        };
    }

}
