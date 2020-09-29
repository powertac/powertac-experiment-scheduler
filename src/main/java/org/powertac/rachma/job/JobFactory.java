package org.powertac.rachma.job;

import org.powertac.rachma.configuration.ConfigurationParameter;
import org.powertac.rachma.configuration.exception.ParameterValidationException;
import org.powertac.rachma.instance.Instance;
import org.powertac.rachma.powertac.broker.exception.BrokerNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface JobFactory<T extends Job> {

    T create(String name, List<String> brokerNames, Set<ConfigurationParameter> simulationParameters)
        throws BrokerNotFoundException, ParameterValidationException, IOException;

    T create(Instance instance) throws BrokerNotFoundException, ParameterValidationException, IOException;

}
