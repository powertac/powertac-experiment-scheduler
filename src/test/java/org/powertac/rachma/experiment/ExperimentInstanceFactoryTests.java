package org.powertac.rachma.experiment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powertac.rachma.instance.Instance;
import org.powertac.rachma.instance.InstanceDuplicator;
import org.powertac.rachma.treatment.Treatment;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ExperimentInstanceFactoryTests {

    @Test
    @SuppressWarnings("unchecked")
    void createExperimentInstancesTest() {
        InstanceDuplicator instanceDuplicator = Mockito.mock(InstanceDuplicator.class);
        ExperimentInstanceFactory instanceFactory = new SimpleExperimentInstanceFactory(instanceDuplicator);

        // Baseline mocks
        Instance base1 = Mockito.mock(Instance.class);
        Instance base2 = Mockito.mock(Instance.class);
        Instance base3 = Mockito.mock(Instance.class);
        List<Instance> baseline = Stream.of(base1, base2, base3).collect(Collectors.toList());

        // Copies of baseline instances
        Instance base1Copy1 = Mockito.mock(Instance.class);
        Instance base1Copy2 = Mockito.mock(Instance.class);
        Instance base1Copy3 = Mockito.mock(Instance.class);
        Mockito.when(instanceDuplicator.createCopy(base1))
            .thenReturn(base1Copy1)
            .thenReturn(base1Copy2)
            .thenReturn(base1Copy3);
        Instance base2Copy1 = Mockito.mock(Instance.class);
        Instance base2Copy2 = Mockito.mock(Instance.class);
        Instance base2Copy3 = Mockito.mock(Instance.class);
        Mockito.when(instanceDuplicator.createCopy(base2))
            .thenReturn(base2Copy1)
            .thenReturn(base2Copy2)
            .thenReturn(base2Copy3);
        Instance base3Copy1 = Mockito.mock(Instance.class);
        Instance base3Copy2 = Mockito.mock(Instance.class);
        Instance base3Copy3 = Mockito.mock(Instance.class);
        Mockito.when(instanceDuplicator.createCopy(base3))
            .thenReturn(base3Copy1)
            .thenReturn(base3Copy2)
            .thenReturn(base3Copy3);

        // Treatments
        Treatment treatment1 = Mockito.mock(Treatment.class);
        Treatment treatment2 = Mockito.mock(Treatment.class);
        Treatment treatment3 = Mockito.mock(Treatment.class);
        List<Treatment> treatments = Stream.of(treatment1, treatment2, treatment3).collect(Collectors.toList());

        // final instances
        Instance base1treat1 = Mockito.mock(Instance.class);
        Instance base1treat2 = Mockito.mock(Instance.class);
        Instance base1treat3 = Mockito.mock(Instance.class);
        Mockito.when(treatment1.mutate(base1Copy1)).thenReturn(base1treat1);
        Mockito.when(treatment2.mutate(base1Copy2)).thenReturn(base1treat2);
        Mockito.when(treatment3.mutate(base1Copy3)).thenReturn(base1treat3);
        Instance base2treat1 = Mockito.mock(Instance.class);
        Instance base2treat2 = Mockito.mock(Instance.class);
        Instance base2treat3 = Mockito.mock(Instance.class);
        Mockito.when(treatment1.mutate(base2Copy1)).thenReturn(base2treat1);
        Mockito.when(treatment2.mutate(base2Copy2)).thenReturn(base2treat2);
        Mockito.when(treatment3.mutate(base2Copy3)).thenReturn(base2treat3);
        Instance base3treat1 = Mockito.mock(Instance.class);
        Instance base3treat2 = Mockito.mock(Instance.class);
        Instance base3treat3 = Mockito.mock(Instance.class);
        Mockito.when(treatment1.mutate(base3Copy1)).thenReturn(base3treat1);
        Mockito.when(treatment2.mutate(base3Copy2)).thenReturn(base3treat2);
        Mockito.when(treatment3.mutate(base3Copy3)).thenReturn(base3treat3);

        Experiment experiment = new ExperimentImpl(
            "Hans Peter",
            baseline,
            treatments);

        List<Instance> instances = instanceFactory.createInstances(experiment);

        // should be 9 instances in total
        Assertions.assertEquals(9, instances.size());

        // ... therefore we need 3 copies of all 3 baseline instances
        Mockito.verify(instanceDuplicator, Mockito.times(3)).createCopy(base1);
        Mockito.verify(instanceDuplicator, Mockito.times(3)).createCopy(base2);
        Mockito.verify(instanceDuplicator, Mockito.times(3)).createCopy(base3);

        // ... each of those copies must then be treated
        Mockito.verify(treatment1).mutate(base1Copy1);
        Mockito.verify(treatment1).mutate(base2Copy1);
        Mockito.verify(treatment1).mutate(base3Copy1);
        Mockito.verify(treatment2).mutate(base1Copy2);
        Mockito.verify(treatment2).mutate(base2Copy2);
        Mockito.verify(treatment2).mutate(base3Copy2);
        Mockito.verify(treatment3).mutate(base1Copy3);
        Mockito.verify(treatment3).mutate(base2Copy3);
        Mockito.verify(treatment3).mutate(base3Copy3);

        // ... and finally the treated instance copies must be included in the resulting lists
        Assertions.assertTrue(instances.contains(base1treat1));
        Assertions.assertTrue(instances.contains(base1treat2));
        Assertions.assertTrue(instances.contains(base1treat3));
        Assertions.assertTrue(instances.contains(base2treat1));
        Assertions.assertTrue(instances.contains(base2treat2));
        Assertions.assertTrue(instances.contains(base2treat3));
        Assertions.assertTrue(instances.contains(base3treat1));
        Assertions.assertTrue(instances.contains(base3treat2));
        Assertions.assertTrue(instances.contains(base3treat3));
    }

}
