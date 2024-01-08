package org.powertac.orchestrator.game;

import org.powertac.orchestrator.persistence.JpaGameRunRepository;
import org.powertac.orchestrator.util.ID;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class PersistentGameRunRepository implements GameRunRepository {

    private final JpaGameRunRepository crudRepository;
    private final GameRepository games;

    public PersistentGameRunRepository(JpaGameRunRepository crudRepository, GameRepository games) {
        this.crudRepository = crudRepository;
        this.games = games;
    }

    @Override
    public GameRun find(String id) {
        return crudRepository.findById(id).orElse(null);
    }

    @Override
    public GameRun create(Game game) {
        GameRun run = new GameRun(ID.gen(), game);
        run.setPhase(GameRunPhase.NONE);
        game.getRuns().add(run);
        this.save(run);
        return run;
    }

    @Override
    public void save(GameRun run) {
        // crudRepository.save(run);
        games.save(run.getGame());
    }

    @Override
    public Collection<GameRun> findFailed() {
        return crudRepository.findAllByFailed(true);
    }

    public void delete(Collection<GameRun> runs) {
        crudRepository.deleteAll(runs);
    }

}
