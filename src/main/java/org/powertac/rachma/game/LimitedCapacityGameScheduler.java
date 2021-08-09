package org.powertac.rachma.game;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Component
public class LimitedCapacityGameScheduler implements GameScheduler {

    private final static int maximumCapacity = 1;

    private final GameRunner runner;
    private final GameSchedule schedule;

    private final ExecutorService gamePool;
    private final Map<Game, Future<Game>> running;

    public LimitedCapacityGameScheduler(GameRunner runner, GameSchedule schedule) {
        this.runner = runner;
        this.schedule = schedule;
        gamePool = Executors.newFixedThreadPool(maximumCapacity);
        running = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized void runGames() {
        updateRunningGames();
        if(hasCapacity()) {
            Game next = schedule.next();
            if (null != next) {
                running.put(next, gamePool.submit(createRun(next)));
            }
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

    private Callable<Game> createRun(Game game) {
        return () -> {
            runner.run(game);
            return game;
        };
    }

}
