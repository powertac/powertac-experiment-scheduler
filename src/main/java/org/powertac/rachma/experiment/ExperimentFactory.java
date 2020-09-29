package org.powertac.rachma.experiment;

import org.powertac.rachma.experiment.exception.ExperimentException;
import org.powertac.rachma.instance.Instance;
import org.powertac.rachma.treatment.Treatment;

import java.util.List;

public interface ExperimentFactory {

    Experiment create(String name, List<Instance> baseline, List<Treatment> treatments) throws ExperimentException;

}
