package org.powertac.rachma.baseline;

import org.powertac.rachma.game.Game;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PersistentBaselineRepository implements BaselineRepository {

    private final BaselineCrudRepository crudRepository;
    private final BaselinePublisher publisher;

    public PersistentBaselineRepository(BaselineCrudRepository crudRepository, BaselinePublisher publisher) {
        this.crudRepository = crudRepository;
        this.publisher = publisher;
    }

    @Override
    public Iterable<Baseline> findAll() {
        return crudRepository.findAll();
    }

    @Override
    public void save(Baseline baseline) {
        crudRepository.save(baseline);
        publisher.publish(baseline);
    }

    @Override
    public String getBaselineIdByGame(Game game) {
        return crudRepository.getBaselineIdByGameId(game.getId());
    }

    @Override
    public Optional<Baseline> findByName(String name) {
        return crudRepository.findByName(name);
    }

    @Override
    public void delete(Baseline baseline) {
        crudRepository.delete(baseline);
    }

}
