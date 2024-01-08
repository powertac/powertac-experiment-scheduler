package org.powertac.orchestrator.baseline;

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
    public Optional<Baseline> findById(String id) {
        return crudRepository.findById(id);
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
