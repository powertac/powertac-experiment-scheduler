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
        int baselineIndex = 0;
        for (Instance baselineInstance : experiment.getBaseline()) {
            int treatmentIndex = 0;
            for (Treatment treatment : experiment.getTreatments()) {
                String name = getInstanceName(experiment.getName(), baselineIndex, treatmentIndex);
                Instance instanceCopy = instanceDuplicator.createNamedCopy(name, baselineInstance);
                instances.add(treatment.mutate(instanceCopy));
                treatmentIndex++;
            }
            baselineIndex++;
        }
        return instances;
    }

    private String getInstanceName(String experimentName, int baselineIndex, int treatmentIndex) {
        return String.format("%s - %s%d",
            experimentName,
            getAlphaIndex(baselineIndex),
            treatmentIndex);
    }

    private Character getAlphaIndex(int index) {
        return "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()[index % 26];
    }

}
