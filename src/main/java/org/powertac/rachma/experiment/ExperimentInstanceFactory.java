package org.powertac.rachma.experiment;

import org.powertac.rachma.instance.Instance;

import java.util.List;

public interface ExperimentInstanceFactory {

    List<Instance> createInstances(Experiment experiment);

}
