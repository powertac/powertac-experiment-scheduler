package org.powertac.rachma.experiment;

import java.util.List;

public interface ExperimentRepository {

    List<Experiment> findAll();
    Experiment findByInstanceId(String instanceId);
    void add(Experiment experiment);

}
