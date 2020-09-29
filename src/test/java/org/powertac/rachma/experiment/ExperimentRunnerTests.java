package org.powertac.rachma.experiment;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powertac.rachma.experiment.exception.ExperimentException;
import org.powertac.rachma.hash.HashProvider;
import org.powertac.rachma.instance.Instance;
import org.powertac.rachma.treatment.Treatment;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ExperimentRunnerTests {

    @Test
    @SuppressWarnings("unchecked")
    void startQueuesAllInstancesWithTheOrchestratorTest() throws ExperimentException {
        ExperimentRunner runner = new OrchestratorExperimentRunner();
        HashProvider<Experiment> hashProvider = Mockito.mock(HashProvider.class);
        ExperimentInstanceFactory instanceFactory = Mockito.mock(ExperimentInstanceFactory.class);
        ExperimentFactory factory = new SimpleExperimentFactory(hashProvider, instanceFactory);

        Instance base1 = Mockito.mock(Instance.class);
        Instance base2 = Mockito.mock(Instance.class);
        Instance base3 = Mockito.mock(Instance.class);
        List<Instance> baseline = Stream.of(base1, base2, base3).collect(Collectors.toList());

        Treatment treatment1 = Mockito.mock(Treatment.class);
        Treatment treatment2 = Mockito.mock(Treatment.class);
        List<Treatment> treatments = Stream.of(treatment1, treatment2).collect(Collectors.toList());

        Experiment experiment = factory.create(
            "important-experiment",
            baseline,
            treatments);

        List<Instance> jobInstances = runner.start(experiment);

        // TODO : this is not a test...?!?
    }

}
