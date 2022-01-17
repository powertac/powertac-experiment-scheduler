package org.powertac.rachma.game;

import org.powertac.rachma.persistence.JpaGameRunRepository;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

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
        String id = UUID.randomUUID().toString();
        GameRun run = new GameRun(id, game);
        game.getRuns().add(run);
        this.save(run);
        return run;
    }

    @Override
    public void save(GameRun run) {
        crudRepository.save(run);
        games.save(run.getGame());
    }

    public void delete(Collection<GameRun> runs) {
        crudRepository.deleteAll(runs);
    }

}
