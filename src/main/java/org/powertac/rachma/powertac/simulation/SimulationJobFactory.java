package org.powertac.rachma.powertac.simulation;

import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.configuration.ConfigurationParameter;
import org.powertac.rachma.configuration.exception.ParameterValidationException;
import org.powertac.rachma.instance.Instance;
import org.powertac.rachma.job.Job;
import org.powertac.rachma.job.JobFactory;
import org.powertac.rachma.powertac.bootstrap.BootstrapTask;
import org.powertac.rachma.powertac.broker.exception.BrokerNotFoundException;
import org.powertac.rachma.resource.WorkDirectoryManager;
import org.powertac.rachma.util.IdGenerator;
import org.powertac.rachma.util.IdProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SimulationJobFactory implements JobFactory<SimulationJob> {

    private final IdProvider idProvider;
    private final WorkDirectoryManager workDirectoryManager;
    private final SimulationTaskFactory simulationTaskFactory;

    @Autowired
    public SimulationJobFactory(IdProvider idProvider, WorkDirectoryManager workDirectoryManager,
                                SimulationTaskFactory simulationTaskFactory) {
        this.idProvider = idProvider;
        this.workDirectoryManager = workDirectoryManager;
        this.simulationTaskFactory = simulationTaskFactory;
    }

    public SimulationJob create(String name, List<String> brokerNames, Set<ConfigurationParameter> simulationParameters)
            throws BrokerNotFoundException, ParameterValidationException, IOException {
        SimulationJob job = new SimulationJob();
        job.setId(idProvider.getAnyId());
        job.setName(name);
        job.setWorkDirectory(workDirectoryManager.create(job));
        job.setBootstrapTask(createBootstrapTask(job));
        job.setSimulationTask(simulationTaskFactory.create(job, brokerNames, parseConfigMap(simulationParameters)));
        return job;
    }

    @Override
    public SimulationJob create(Instance instance) throws BrokerNotFoundException, ParameterValidationException, IOException {
        List<String> brokerNames = instance.getBrokers().stream()
            .map(Broker::getName)
            .collect(Collectors.toList());
        Set<ConfigurationParameter> configurationParameters = instance.getServerParameters().toConfigurationParameterSet();
        // TODO : how to derive/pass name for instance?
        return create(
            "any name",
            brokerNames,
            configurationParameters);
    }

    private BootstrapTask createBootstrapTask(Job job) {
        return new BootstrapTask(IdGenerator.generateId(), job);
    }

    private Map<String, String> parseConfigMap(Set<ConfigurationParameter> parameters) {
        Map<String, String> parameterMap = new HashMap<>();
        for (ConfigurationParameter parameter : parameters) {
            parameterMap.put(parameter.getParameter(), parameter.getValue());
        }
        return parameterMap;
    }

}
