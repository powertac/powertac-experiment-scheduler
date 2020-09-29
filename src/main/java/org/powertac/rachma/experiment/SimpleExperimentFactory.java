package org.powertac.rachma.experiment;

import org.powertac.rachma.experiment.exception.ExperimentException;
import org.powertac.rachma.experiment.exception.InvalidBaselineException;
import org.powertac.rachma.experiment.exception.InvalidTreatmentException;
import org.powertac.rachma.hash.HashProvider;
import org.powertac.rachma.instance.Instance;
import org.powertac.rachma.treatment.Treatment;

import java.util.List;

public class SimpleExperimentFactory implements ExperimentFactory {

    private final HashProvider<Experiment> hashProvider;
    private final ExperimentInstanceFactory instanceFactory;

    public SimpleExperimentFactory(HashProvider<Experiment> hashProvider, ExperimentInstanceFactory instanceFactory) {
        this.hashProvider = hashProvider;
        this.instanceFactory = instanceFactory;
    }

    @Override
    public Experiment create(String name, List<Instance> baseline, List<Treatment> treatments) throws ExperimentException {
        if (baseline.size() < 1) {
            throw new InvalidBaselineException("the baseline must consist of at least one instance");
        }
        if (treatments.size() < 1) {
            throw new InvalidTreatmentException("there must be at least one treatment");
        }
        ExperimentImpl experiment = new ExperimentImpl(name, baseline, treatments);
        experiment.setHash(hashProvider.getHash(experiment));
        experiment.setInstances(instanceFactory.createInstances(experiment));
        return experiment;
    }

}
