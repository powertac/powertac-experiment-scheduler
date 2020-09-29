package org.powertac.rachma.experiment;

import org.powertac.rachma.instance.Instance;

import java.util.List;

public interface ExperimentRunner {

    List<Instance> start(Experiment experiment);

}
