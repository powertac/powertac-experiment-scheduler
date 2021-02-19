package org.powertac.rachma.experiment;

import org.powertac.rachma.instance.Instance;
import org.powertac.rachma.treatment.Treatment;

import java.util.List;

public interface Experiment {

    String getHash();
    String getName();
    List<Instance> getBaseline();
    List<Treatment> getTreatments();
    List<Instance> getInstances();

}
