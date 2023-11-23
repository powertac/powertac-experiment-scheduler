package org.powertac.rachma.game;

import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class LimitedCapacityGameScheduler implements GameScheduler {

    private final static int maximumCapacity = 1;

    private final GameRunner runner;
    private final GameSchedule schedule;

    private final ExecutorService gamePool;
    private final Map<Game, Future<Game>> running;
    private final AtomicBoolean shutdown;

    public LimitedCapacityGameScheduler(GameRunner runner, GameSchedule schedule) {
        this.runner = runner;
        this.schedule = schedule;
        gamePool = Executors.newFixedThreadPool(maximumCapacity);
        running = new ConcurrentHashMap<>();
        shutdown = new AtomicBoolean(false);
    }

    @Override
    public synchronized void runGames() {
        updateRunningGames();
        if(hasCapacity() && !shutdown.get()) {
            Game next = schedule.next();
            if (null != next) {
                running.put(next, gamePool.submit(createRun(next)));
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        shutdown.set(true);
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
