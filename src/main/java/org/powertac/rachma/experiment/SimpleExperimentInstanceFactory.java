package org.powertac.rachma.experiment;

import org.powertac.rachma.instance.Instance;
import org.powertac.rachma.instance.InstanceDuplicator;
import org.powertac.rachma.treatment.Treatment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SimpleExperimentInstanceFactory implements ExperimentInstanceFactory {

    private final InstanceDuplicator instanceDuplicator;

    public SimpleExperimentInstanceFactory(InstanceDuplicator instanceDuplicator) {
        this.instanceDuplicator = instanceDuplicator;
    }

    @Override
    public List<Instance> createInstances(Experiment experiment) {
        List<Instance> instances = new ArrayList<>();
        for (Instance baselineInstance : experiment.getBaseline()) {
            for (Treatment treatment : experiment.getTreatments()) {
                Instance instanceCopy = instanceDuplicator.createCopy(baselineInstance);
                instances.add(treatment.mutate(instanceCopy));
            }
        }
        return instances;
    }

}
