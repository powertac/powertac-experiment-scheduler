package org.powertac.rachma.experiment;

import java.util.List;

public interface ExperimentRepository {

    List<Experiment> findAll();

    void add(Experiment experiment);

}
