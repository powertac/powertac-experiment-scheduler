package org.powertac.rachma.game;

import org.powertac.rachma.api.stomp.EntityPublisher;
import org.powertac.rachma.persistence.JpaGameRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
public class PersistentGameRepository implements GameRepository {

    private final JpaGameRepository games;
    private final EntityPublisher<Game> publisher;

    public PersistentGameRepository(JpaGameRepository games, EntityPublisher<Game> publisher) {
        this.games = games;
        this.publisher = publisher;
    }

    @Override
    public Collection<Game> findAll() {
        List<Game> gameList = new ArrayList<>();
        games.findAll().forEach(gameList::add);
        return gameList;
    }

    @Override
    public Game findById(String id) {
        return games.findById(id).orElse(null);
    }

    @Override
    public Game findFirstQueued() {
        return games.findFirstQueued();
    }

    @Override
    public void save(Game game) {
        if (null == game.getId()) {
            game.setId(UUID.randomUUID().toString());
        }
        games.save(game);
        publisher.publish(game);
    }

}
