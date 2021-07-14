package org.powertac.rachma.game;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class TransientGameRepository implements GameRepository {

    private final GameRunRepository runs;
    private final Map<String, Game> games;

    public TransientGameRepository(GameRunRepository runs) {
        this.runs = runs;
        games = new HashMap<>();
    }

    @Override
    public Collection<Game> findAll() {
        return games.values();
    }

    @Override
    public Game findById(String id) {
        return games.get(id);
    }

    @Override
    public Game findOneQueued() {
        for (Game game : games.values()) {
            if (!runs.hasSuccessfulRun(game)) {
                return game;
            }
        }
        return null;
    }

    @Override
    public void save(Game game) {
        if (null == game.getId()) {
            game.setId(UUID.randomUUID().toString());
        }
        games.put(game.getId(), game);
    }

}
