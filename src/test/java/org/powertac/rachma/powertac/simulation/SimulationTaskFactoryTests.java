package org.powertac.rachma.powertac.simulation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powertac.rachma.configuration.exception.ParameterValidationException;
import org.powertac.rachma.job.Job;
import org.powertac.rachma.broker.BrokerType;
import org.powertac.rachma.broker.BrokerTypeRepository;
import org.powertac.rachma.powertac.broker.exception.BrokerNotFoundException;
import org.powertac.rachma.util.IdProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class SimulationTaskFactoryTests {

    // TODO : Fix test
    // @Test
    void createSimulationTaskWithoutServerParametersTest() throws BrokerNotFoundException, ParameterValidationException {
        IdProvider idProvider = Mockito.mock(IdProvider.class);
        BrokerTypeRepository brokerTypeRepository = Mockito.mock(BrokerTypeRepository.class);
        SimulationParameterValidator parameterValidator = Mockito.mock(SimulationParameterValidator.class);
        SimulationTaskFactory taskFactory = new SimpleSimulationTaskFactory(idProvider, brokerTypeRepository, parameterValidator);

        String id = "123456abcdef";
        Mockito.when(idProvider.getAnyId()).thenReturn(id);

        Job job = Mockito.mock(Job.class);

        String brokerName1 = "Tanja";
        String brokerName2 = "Hans";
        String brokerName3 = "Ronja";
        List<String> brokerNames = Stream.of(brokerName1, brokerName2, brokerName3).collect(Collectors.toList());

        BrokerType brokerType1 = Mockito.mock(BrokerType.class);
        Mockito.when(brokerTypeRepository.has(brokerName1)).thenReturn(true);
        Mockito.when(brokerTypeRepository.findByName(brokerName1)).thenReturn(brokerType1);
        BrokerType brokerType2 = Mockito.mock(BrokerType.class);
        Mockito.when(brokerTypeRepository.has(brokerName2)).thenReturn(true);
        Mockito.when(brokerTypeRepository.findByName(brokerName2)).thenReturn(brokerType2);
        BrokerType brokerType3 = Mockito.mock(BrokerType.class);
        Mockito.when(brokerTypeRepository.has(brokerName3)).thenReturn(true);
        Mockito.when(brokerTypeRepository.findByName(brokerName3)).thenReturn(brokerType3);

        SimulationTask task = taskFactory.create(job, brokerNames);

        Assertions.assertEquals(id, task.getId());
        Assertions.assertEquals(job, task.getJob());

        Assertions.assertTrue(task.getBrokers().contains(brokerType1));
        Assertions.assertTrue(task.getBrokers().contains(brokerType2));
        Assertions.assertTrue(task.getBrokers().contains(brokerType3));

        Assertions.assertTrue(task.getParameters().isEmpty());
    }

    // TODO : Fix test
    // @Test
    void createSimulationTaskWithServerParametersTest() throws BrokerNotFoundException, ParameterValidationException {
        IdProvider idProvider = Mockito.mock(IdProvider.class);
        BrokerTypeRepository brokerTypeRepository = Mockito.mock(BrokerTypeRepository.class);
        SimulationParameterValidator parameterValidator = Mockito.mock(SimulationParameterValidator.class);
        SimulationTaskFactory taskFactory = new SimpleSimulationTaskFactory(idProvider, brokerTypeRepository, parameterValidator);

        String id = "123456abcdef";
        Mockito.when(idProvider.getAnyId()).thenReturn(id);

        Job job = Mockito.mock(Job.class);

        String brokerName1 = "Tanja";
        String brokerName2 = "Hans";
        String brokerName3 = "Ronja";
        List<String> brokerNames = Stream.of(brokerName1, brokerName2, brokerName3).collect(Collectors.toList());

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("test.parameter.fivehundred", "500");
        parameterMap.put("value.of", "Hans Peter Ludwig");
        parameterMap.put("some.uri", "https://www.youtube.com/watch?v=4MEKu2TcEHM");
        parameterMap.put("a.very.important.date", "2020-02-02");

        BrokerType brokerType1 = Mockito.mock(BrokerType.class);
        Mockito.when(brokerTypeRepository.has(brokerName1)).thenReturn(true);
        Mockito.when(brokerTypeRepository.findByName(brokerName1)).thenReturn(brokerType1);
        BrokerType brokerType2 = Mockito.mock(BrokerType.class);
        Mockito.when(brokerTypeRepository.has(brokerName2)).thenReturn(true);
        Mockito.when(brokerTypeRepository.findByName(brokerName2)).thenReturn(brokerType2);
        BrokerType brokerType3 = Mockito.mock(BrokerType.class);
        Mockito.when(brokerTypeRepository.has(brokerName3)).thenReturn(true);
        Mockito.when(brokerTypeRepository.findByName(brokerName3)).thenReturn(brokerType3);

        SimulationTask task = taskFactory.create(job, brokerNames, parameterMap);

        Assertions.assertEquals(id, task.getId());
        Assertions.assertEquals(job, task.getJob());

        Assertions.assertTrue(task.getBrokers().contains(brokerType1));
        Assertions.assertTrue(task.getBrokers().contains(brokerType2));
        Assertions.assertTrue(task.getBrokers().contains(brokerType3));

        Assertions.assertEquals("500", task.getParameters().get("test.parameter.fivehundred"));
        Assertions.assertEquals("Hans Peter Ludwig", task.getParameters().get("value.of"));
        Assertions.assertEquals("https://www.youtube.com/watch?v=4MEKu2TcEHM", task.getParameters().get("some.uri"));
        Assertions.assertEquals("2020-02-02", task.getParameters().get("a.very.important.date"));
    }

    // TODO : Fix test
    // @Test
    void createSimulationTaskThrowsExceptionWhenNoBrokerIsFoundTest() throws BrokerNotFoundException {
        IdProvider idProvider = Mockito.mock(IdProvider.class);
        BrokerTypeRepository brokerTypeRepository = Mockito.mock(BrokerTypeRepository.class);
        SimulationParameterValidator parameterValidator = Mockito.mock(SimulationParameterValidator.class);
        SimulationTaskFactory taskFactory = new SimpleSimulationTaskFactory(idProvider, brokerTypeRepository, parameterValidator);

        Mockito.when(idProvider.getAnyId()).thenReturn("123456abcdef");

        Job job = Mockito.mock(Job.class);

        String brokerName1 = "Tanja";
        String brokerName2 = "Hans";
        String brokerName3 = "Ronja";
        String brokerName4 = "Klaus";
        List<String> brokerNames = Stream.of(brokerName1, brokerName2, brokerName3, brokerName4)
            .collect(Collectors.toList());

        BrokerType brokerType1 = Mockito.mock(BrokerType.class);
        Mockito.when(brokerTypeRepository.has(brokerName1)).thenReturn(true);
        Mockito.when(brokerTypeRepository.findByName(brokerName1)).thenReturn(brokerType1);
        BrokerType brokerType2 = Mockito.mock(BrokerType.class);
        Mockito.when(brokerTypeRepository.has(brokerName2)).thenReturn(true);
        Mockito.when(brokerTypeRepository.findByName(brokerName2)).thenReturn(brokerType2);
        BrokerType brokerType3 = Mockito.mock(BrokerType.class);
        Mockito.when(brokerTypeRepository.has(brokerName3)).thenReturn(true);
        Mockito.when(brokerTypeRepository.findByName(brokerName3)).thenReturn(brokerType3);
        Mockito.when(brokerTypeRepository.has(brokerName4)).thenReturn(false);

        Assertions.assertThrows(BrokerNotFoundException.class, () -> taskFactory.create(job, brokerNames));
    }

    // TODO : fix test
    // @Test
    void createSimulationTaskThrowsValidationExceptionTest() throws BrokerNotFoundException, ParameterValidationException {
        /*
         * FIXME : Fix this test (see failed test for details)
         */
        IdProvider idProvider = Mockito.mock(IdProvider.class);
        BrokerTypeRepository brokerTypeRepository = Mockito.mock(BrokerTypeRepository.class);
        SimulationParameterValidator parameterValidator = Mockito.mock(SimulationParameterValidator.class);
        SimulationTaskFactory taskFactory = new SimpleSimulationTaskFactory(idProvider, brokerTypeRepository, parameterValidator);

        String id = "123456abcdef";
        Mockito.when(idProvider.getAnyId()).thenReturn(id);

        Job job = Mockito.mock(Job.class);

        String brokerName1 = "Tanja";
        String brokerName2 = "Hans";
        String brokerName3 = "Ronja";
        List<String> brokerNames = Stream.of(brokerName1, brokerName2, brokerName3).collect(Collectors.toList());

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("test.parameter.fivehundred", "500");
        parameterMap.put("value.of", "Hans Peter Ludwig");
        parameterMap.put("some.uri", "https://www.youtube.com/watch?v=4MEKu2TcEHM");
        parameterMap.put("a.very.important.date", "2020-02-02");
        parameterMap.put("this.is.super.invalid", "krkkkrrrkkr");

        Mockito.doThrow(ParameterValidationException.class)
            .when(parameterValidator)
            .validate("this.is.super.invalid", "krkkkrrrkkr");

        BrokerType brokerType1 = Mockito.mock(BrokerType.class);
        Mockito.when(brokerTypeRepository.has(brokerName1)).thenReturn(true);
        Mockito.when(brokerTypeRepository.findByName(brokerName1)).thenReturn(brokerType1);
        BrokerType brokerType2 = Mockito.mock(BrokerType.class);
        Mockito.when(brokerTypeRepository.has(brokerName2)).thenReturn(true);
        Mockito.when(brokerTypeRepository.findByName(brokerName2)).thenReturn(brokerType2);
        BrokerType brokerType3 = Mockito.mock(BrokerType.class);
        Mockito.when(brokerTypeRepository.has(brokerName3)).thenReturn(true);
        Mockito.when(brokerTypeRepository.findByName(brokerName3)).thenReturn(brokerType3);

        Assertions.assertThrows(ParameterValidationException.class,
            () -> taskFactory.create(job, brokerNames, parameterMap));
    }

}
