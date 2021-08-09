package org.powertac.rachma.game;

import org.powertac.rachma.api.stomp.StompMessageBroker;
import org.powertac.rachma.persistence.JpaGameRepository;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
public class PersistentGameRepository implements GameRepository {

    private final JpaGameRepository games;
    private final StompMessageBroker<Game> messages;

    public PersistentGameRepository(JpaGameRepository games, StompMessageBroker<Game> messages) {
        this.games = games;
        this.messages = messages;
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
        messages.publish(game);
    }

}
