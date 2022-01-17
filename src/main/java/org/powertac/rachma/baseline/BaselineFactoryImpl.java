package org.powertac.rachma.baseline;

import org.powertac.rachma.broker.BrokerSet;
import org.powertac.rachma.broker.BrokerSetFactory;
import org.powertac.rachma.validation.SimulationParameterValidator;
import org.powertac.rachma.validation.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class BaselineFactoryImpl implements BaselineFactory {

    private final SimulationParameterValidator parameterValidator;
    private final BrokerSetFactory brokerSetFactory;

    public BaselineFactoryImpl(SimulationParameterValidator parameterValidator, BrokerSetFactory brokerSetFactory) {
        this.parameterValidator = parameterValidator;
        this.brokerSetFactory = brokerSetFactory;
    }

    @Override
    public Baseline createFromSpec(BaselineSpec spec) throws ValidationException {
        validateServerParameters(spec.getCommonParameters());
        return new Baseline(
            UUID.randomUUID().toString(),
            spec.getName(),
            spec.getCommonParameters(),
            createNewBrokerSets(spec.getBrokerSets()),
            spec.getWeatherConfigurations(), // TODO : validate weather configurations
            new ArrayList<>(),
            Instant.now());
    }

    private List<BrokerSet> createNewBrokerSets(List<BrokerSet> sets) throws ValidationException {
        // TODO : check for duplicate broker sets
        List<BrokerSet> brokerSets = new ArrayList<>();
        for (BrokerSet set : sets) {
            brokerSets.add(null == set.getId()
                ? brokerSetFactory.create(set.getBrokers())
                : set);
        }
        return brokerSets;
    }

    private void validateServerParameters(Map<String, String> parameters) throws ValidationException {
        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            parameterValidator.validate(parameter.getKey(), parameter.getValue());
        }
    }

}
