package org.powertac.rachma.powertac.simulation;

import org.powertac.rachma.configuration.exception.ParameterValidationException;
import org.powertac.rachma.job.Job;
import org.powertac.rachma.broker.BrokerType;
import org.powertac.rachma.broker.BrokerTypeRepository;
import org.powertac.rachma.powertac.broker.exception.BrokerNotFoundException;
import org.powertac.rachma.util.IdProvider;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SimpleSimulationTaskFactory implements SimulationTaskFactory {

    private final IdProvider idProvider;
    private final BrokerTypeRepository brokerTypeRepository;
    private final SimulationParameterValidator parameterValidator;

    public SimpleSimulationTaskFactory(IdProvider idProvider, BrokerTypeRepository brokerTypeRepository,
                                       SimulationParameterValidator parameterValidator) {
        this.idProvider = idProvider;
        this.brokerTypeRepository = brokerTypeRepository;
        this.parameterValidator = parameterValidator;
    }

    @Override
    public SimulationTask create(Job job, List<String> brokerNames)
            throws BrokerNotFoundException, ParameterValidationException {
        return create(job, brokerNames, new HashMap<>());
    }

    @Override
    public SimulationTask create(Job job, List<String> brokerNames, Map<String, String> parameters)
            throws BrokerNotFoundException, ParameterValidationException {
        return create(job, brokerNames, parameters, null, null);
    }

    @Override
    public SimulationTask create(Job job, List<String> brokerNames, Map<String, String> parameters, String bootstrapFile, String seedFile)
            throws BrokerNotFoundException, ParameterValidationException {
        Set<BrokerType> brokerTypes = findBrokerTypesByNames(brokerNames);
        validateServerParameters(parameters);
        return new SimulationTask(
            idProvider.getAnyId(),
            job,
            brokerTypes,
            parameters,
            bootstrapFile,
            seedFile);
    }

    private Set<BrokerType> findBrokerTypesByNames(List<String> brokerNames) throws BrokerNotFoundException {
        Set<BrokerType> types = new HashSet<>();
        for (String brokerName : brokerNames) {
            if (!brokerTypeRepository.has(brokerName)) {
                throw new BrokerNotFoundException(
                    String.format("could not find broker with name '%s'", brokerName));
            }
            types.add(brokerTypeRepository.findByName(brokerName));
        }
        return types;
    }

    private void validateServerParameters(Map<String, String> serverParameters) throws ParameterValidationException {
        for (Map.Entry<String, String> entry : serverParameters.entrySet()) {
            parameterValidator.validate(entry.getKey(), entry.getValue());
        }
    }

}
