package org.powertac.rachma.job;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.configuration.exception.ParameterValidationException;
import org.powertac.rachma.instance.Instance;
import org.powertac.rachma.instance.ServerParameters;
import org.powertac.rachma.instance.TransientServerParameters;
import org.powertac.rachma.powertac.broker.exception.BrokerNotFoundException;
import org.powertac.rachma.powertac.simulation.SimulationJob;
import org.powertac.rachma.powertac.simulation.SimulationJobFactory;
import org.powertac.rachma.powertac.simulation.SimulationTaskFactory;
import org.powertac.rachma.resource.WorkDirectory;
import org.powertac.rachma.resource.WorkDirectoryManager;
import org.powertac.rachma.util.IdProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class JobFactoryTests {

    @Test
    @SuppressWarnings("unchecked")
    void createFromInstanceTest() throws BrokerNotFoundException, ParameterValidationException, IOException {
        IdProvider idProvider = Mockito.mock(IdProvider.class);
        SimulationTaskFactory simulationTaskFactory = Mockito.mock(SimulationTaskFactory.class);
        WorkDirectoryManager workDirectoryManager = Mockito.mock(WorkDirectoryManager.class);

        JobFactory<SimulationJob> factory = new SimulationJobFactory(
            idProvider,
            workDirectoryManager,
            simulationTaskFactory);

        String id = "123456abcdef";
        Mockito.when(idProvider.getAnyId()).thenReturn(id);

        WorkDirectory workDirectory = new WorkDirectory(
            "/local/directory/123",
            "/host/directory/123");

        Mockito.when(workDirectoryManager.create(Mockito.any(Job.class))).thenReturn(workDirectory);

        String broker1Name = "JeanJaques";
        String broker2Name = "ClaudetteBroker";
        String broker3Name = "ElephantAgent";

        Broker broker1 = Mockito.mock(Broker.class);
        Broker broker2 = Mockito.mock(Broker.class);
        Broker broker3 = Mockito.mock(Broker.class);
        Mockito.when(broker1.getName()).thenReturn(broker1Name);
        Mockito.when(broker2.getName()).thenReturn(broker2Name);
        Mockito.when(broker3.getName()).thenReturn(broker3Name);
        Set<Broker> brokers = Stream.of(broker1, broker2, broker3).collect(Collectors.toSet());

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("test.parameter.fivehundred", "500");
        parameterMap.put("value.of", "Hans Peter Ludwig");
        parameterMap.put("some.uri", "https://www.youtube.com/watch?v=4MEKu2TcEHM");
        parameterMap.put("a.very.important.date", "2020-02-02");
        ServerParameters parameters = new TransientServerParameters(parameterMap);

        Instance instance = Mockito.mock(Instance.class);
        Mockito.when(instance.getId()).thenReturn(id);
        Mockito.when(instance.getBrokers()).thenReturn(brokers);
        Mockito.when(instance.getServerParameters()).thenReturn(parameters);

        Mockito.doAnswer(invocation -> {
            List<String> passedBrokerNames = (List<String>) invocation.getArgumentAt(1, List.class);
            Assertions.assertTrue(passedBrokerNames.contains(broker1Name));
            Assertions.assertTrue(passedBrokerNames.contains(broker2Name));
            Assertions.assertTrue(passedBrokerNames.contains(broker3Name));
            Map<String, String> passedServerParameters = (Map<String, String>) invocation.getArgumentAt(2, Map.class);
            Assertions.assertEquals("500", passedServerParameters.get("test.parameter.fivehundred"));
            Assertions.assertEquals("Hans Peter Ludwig", passedServerParameters.get("value.of"));
            Assertions.assertEquals("https://www.youtube.com/watch?v=4MEKu2TcEHM", passedServerParameters.get("some.uri"));
            Assertions.assertEquals("2020-02-02", passedServerParameters.get("a.very.important.date"));
            return Mockito.any();
        }).when(simulationTaskFactory).create(Mockito.any(Job.class), Mockito.anyList(), Mockito.anyMap());

        SimulationJob job = factory.create(instance);
        // TODO : add name to instance
        Assertions.assertEquals(id, job.getId());
        Assertions.assertEquals(workDirectory, job.getWorkDirectory());
        Assertions.assertNotNull(job.getBootstrapTask());
        Assertions.assertEquals(job, job.getBootstrapTask().getJob());
    }

}
