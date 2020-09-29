package org.powertac.rachma.experiment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powertac.rachma.experiment.exception.ExperimentException;
import org.powertac.rachma.experiment.exception.InvalidBaselineException;
import org.powertac.rachma.experiment.exception.InvalidTreatmentException;
import org.powertac.rachma.hash.HashProvider;
import org.powertac.rachma.instance.Instance;
import org.powertac.rachma.treatment.Treatment;

import java.util.ArrayList;
import java.util.List;

public class ExperimentFactoryTests {

    @Test
    @SuppressWarnings("unchecked")
    void createReturnsValidExperimentObjectTest() throws ExperimentException {
        ExperimentInstanceFactory instanceFactory = Mockito.mock(ExperimentInstanceFactory.class);
        HashProvider<Experiment> hashProvider = (HashProvider<Experiment>) Mockito.mock(HashProvider.class);
        ExperimentFactory factory = new SimpleExperimentFactory(hashProvider, instanceFactory);

        String hash = "123456abcdef";
        String name = "Hans Jolo";
        List<Instance> baseline = new ArrayList<>();
        baseline.add(Mockito.mock(Instance.class));
        List<Treatment> treatments = new ArrayList<>();
        treatments.add((instance) -> instance);

        Mockito.when(hashProvider.getHash(Mockito.any(Experiment.class))).thenReturn(hash);

        Experiment experiment = factory.create(name, baseline, treatments);

        Assertions.assertEquals(hash, experiment.getHash());
        Assertions.assertEquals(name, experiment.getName());
        Assertions.assertTrue(baseline.containsAll(experiment.getBaseline()));
        Assertions.assertTrue(treatments.containsAll(experiment.getTreatments()));
        Mockito.verify(instanceFactory).createInstances(experiment);
    }

    @Test
    @SuppressWarnings("unchecked")
    void createThrowsExceptionIfTheBaselineIsEmptyTest() {
        ExperimentInstanceFactory instanceFactory = Mockito.mock(ExperimentInstanceFactory.class);
        HashProvider<Experiment> hashProvider = (HashProvider<Experiment>) Mockito.mock(HashProvider.class);
        ExperimentFactory factory = new SimpleExperimentFactory(hashProvider, instanceFactory);

        String hash = "fedcba987456";
        List<Instance> baseline = new ArrayList<>();
        List<Treatment> treatments = new ArrayList<>();

        Mockito.when(hashProvider.getHash(Mockito.any(Experiment.class))).thenReturn(hash);

        Assertions.assertThrows(InvalidBaselineException.class, () -> factory.create(
            "Garles de Chaulle", baseline, treatments));
    }

    @Test
    @SuppressWarnings("unchecked")
    void createThrowsExceptionIfThereAreNoTreatments() {
        ExperimentInstanceFactory instanceFactory = Mockito.mock(ExperimentInstanceFactory.class);
        HashProvider<Experiment> hashProvider = (HashProvider<Experiment>) Mockito.mock(HashProvider.class);
        ExperimentFactory factory = new SimpleExperimentFactory(hashProvider, instanceFactory);

        String hash = "fedcba987456";
        List<Instance> baseline = new ArrayList<>();
        baseline.add(Mockito.mock(Instance.class));
        List<Treatment> treatments = new ArrayList<>();

        Mockito.when(hashProvider.getHash(Mockito.any(Experiment.class))).thenReturn(hash);

        Assertions.assertThrows(InvalidTreatmentException.class, () -> factory.create(
            "Feter Pox", baseline, treatments));
    }

}
