package org.powertac.rachma.powertac.simulation;

import org.powertac.rachma.configuration.exception.ParameterValidationException;
import org.powertac.rachma.job.Job;
import org.powertac.rachma.powertac.broker.exception.BrokerNotFoundException;

import java.util.List;
import java.util.Map;

public interface SimulationTaskFactory {

    SimulationTask create(Job job, List<String> brokerNames)
        throws BrokerNotFoundException, ParameterValidationException;

    SimulationTask create(Job job, List<String> brokerNames, Map<String, String> parameters)
        throws BrokerNotFoundException, ParameterValidationException;

}
