package org.powertac.rachma.game;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TransientGameRunRepository implements GameRunRepository {

    private final Map<String, GameRun> runs;

    public TransientGameRunRepository() {
        runs = new HashMap<>();
    }

    @Override
    public GameRun create(Game game) {
        String id = UUID.randomUUID().toString();
        GameRun run = new GameRun(id, game);
        runs.put(id, run);
        return run;
    }

    @Override
    public void update(GameRun run) {
        runs.put(run.getId(), run);
    }

    @Override
    public Collection<GameRun> findActiveByGame(Game game) {
        Set<GameRun> activeRuns = new HashSet<>();
        for (GameRun run : runs.values()) {
            if (run.getGame().equals(game) && isActive(run)) {
                activeRuns.add(run);
            }
        }
        return activeRuns;
    }

    @Override
    public boolean hasSuccessfulRun(Game game) {
        for (GameRun run : runs.values()) {
            if (run.getGame().equals(game) && completedSuccessfully(run)) {
                return true;
            }
        }
        return false;
    }

    public boolean completedSuccessfully(GameRun run) {
        return run.getPhase().equals(GameRunPhase.DONE)
            && !run.hasFailed();
    }

    private boolean isActive(GameRun run) {
        return !run.getPhase().equals(GameRunPhase.NONE)
            && !run.getPhase().equals(GameRunPhase.DONE);
    }

}
