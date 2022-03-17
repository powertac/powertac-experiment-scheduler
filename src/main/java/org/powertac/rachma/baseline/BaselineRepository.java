package org.powertac.rachma.baseline;

import java.util.Optional;

public interface BaselineRepository {

    Iterable<Baseline> findAll();
    Optional<Baseline> findById(String id);
    Optional<Baseline> findByName(String name);
    void save(Baseline baseline);
    void delete(Baseline baseline);

}
