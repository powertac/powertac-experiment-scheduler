package org.powertac.rachma.game;

import org.powertac.rachma.persistence.JpaGameRunRepository;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Component
public class PersistentGameRunRepository implements GameRunRepository {

    private final JpaGameRunRepository runs;
    private final GameRepository games;

    public PersistentGameRunRepository(JpaGameRunRepository runs, GameRepository games) {
        this.runs = runs;
        this.games = games;
    }

    @Override
    public GameRun create(Game game) {
        String id = UUID.randomUUID().toString();
        GameRun run = new GameRun(id, game);
        game.getRuns().add(run);
        this.save(run);
        return run;
    }

    @Override
    public void save(GameRun run) {
        runs.save(run);
        games.save(run.getGame());
    }

    @Override
    public Collection<GameRun> findActiveByGame(Game game) {
        return runs.findAllByGameAndPhaseBetween(game, GameRunPhase.PREPARATION, GameRunPhase.SIMULATION);
    }

    @Override
    public boolean hasSuccessfulRun(Game game) {
        return runs.existsByGameAndPhaseAndFailed(game, GameRunPhase.DONE, false);
    }

    @Override
    public boolean hasActiveRun(Game game) {
        return runs.existsByGameAndPhaseNot(game, GameRunPhase.DONE);
    }
}
