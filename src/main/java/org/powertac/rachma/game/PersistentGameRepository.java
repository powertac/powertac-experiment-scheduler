package org.powertac.rachma.game;

import org.powertac.rachma.api.stomp.EntityPublisher;
import org.powertac.rachma.persistence.JpaGameRepository;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

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
        gameList.forEach(this::removeDuplicateGameRuns);
        return gameList;
    }

    @Override
    public Game findById(String id) {
        Game game = games.findById(id).orElse(null);
        if (null != game) {
            removeDuplicateGameRuns(game);
        }
        return game;
    }

    @Override
    public Game findOneByName(String name) {
        return games.findOneByName(name).orElse(null);
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

    @Override
    public boolean exists(String id) {
        return games.existsById(id);
    }

    @Override
    public void delete(Game game) {
        games.delete(game);
    }

    private void removeDuplicateGameRuns(Game game) {
        game.setRuns(new HashSet<>(game.getRuns()).stream()
            .sorted(Comparator.comparing(GameRun::getStart))
            .sorted(Collections.reverseOrder())
            .collect(Collectors.toList()));
    }

}
