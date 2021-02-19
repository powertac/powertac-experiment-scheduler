package org.powertac.rachma.experiment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powertac.rachma.hash.HashProvider;
import org.powertac.rachma.instance.Instance;
import org.powertac.rachma.treatment.Treatment;

import java.util.ArrayList;
import java.util.List;

public class ExperimentHashProviderTests {

    // TODO : Fix test
    // @Test
    @SuppressWarnings("unchecked")
    void hashReturnsCorrectValueTest() {
        HashProvider<Instance> instanceHasher = (HashProvider<Instance>) Mockito.mock(HashProvider.class);
        HashProvider<Treatment> treatmentHasher = (HashProvider<Treatment>) Mockito.mock(HashProvider.class);
        HashProvider<Experiment> hasher = new Sha256ExperimentHashProvider(instanceHasher, treatmentHasher);

        List<Instance> baseline = new ArrayList<>();
        baseline.add(Mockito.mock(Instance.class));
        baseline.add(Mockito.mock(Instance.class));
        baseline.add(Mockito.mock(Instance.class));
        List<Treatment> treatments = new ArrayList<>();
        treatments.add(Mockito.mock(Treatment.class));
        treatments.add(Mockito.mock(Treatment.class));
        treatments.add(Mockito.mock(Treatment.class));
        treatments.add(Mockito.mock(Treatment.class));

        Mockito.when(instanceHasher.getHash(Mockito.any(Instance.class)))
            .thenReturn("a".repeat(64))
            .thenReturn("b".repeat(64))
            .thenReturn("c".repeat(64));
        Mockito.when(treatmentHasher.getHash(Mockito.any(Treatment.class)))
            .thenReturn("d".repeat(64))
            .thenReturn("e".repeat(64))
            .thenReturn("f".repeat(64))
            .thenReturn("g".repeat(64));

        Experiment experiment = new ExperimentImpl("Something", baseline, treatments);

        String hash = hasher.getHash(experiment);

        Assertions.assertEquals("37d995fcdb20151e1f8170053b225d3016dede337c0ca8b29519523c1dc2631b", hash);
    }

}
