package org.powertac.rachma.baseline;

import org.powertac.rachma.broker.BrokerSet;
import org.powertac.rachma.broker.BrokerSetFactory;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.generator.GameGeneratorConfig;
import org.powertac.rachma.game.generator.MultiplierGameGenerator;
import org.powertac.rachma.game.generator.MultiplierGameGeneratorConfig;
import org.powertac.rachma.util.ID;
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
    private final MultiplierGameGenerator multiplierGameGenerator;

    public BaselineFactoryImpl(SimulationParameterValidator parameterValidator, BrokerSetFactory brokerSetFactory, MultiplierGameGenerator multiplierGameGenerator) {
        this.parameterValidator = parameterValidator;
        this.brokerSetFactory = brokerSetFactory;
        this.multiplierGameGenerator = multiplierGameGenerator;
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
            Instant.now()
            , null);
    }

    @Override
    public Baseline generate(String name, GameGeneratorConfig config) throws ValidationException {
        List<Game> games;
        if (config instanceof MultiplierGameGeneratorConfig) {
            MultiplierGameGeneratorConfig multiplierConfig = (MultiplierGameGeneratorConfig) config;
            validateServerParameters(multiplierConfig.getGameConfig().getParameters());
            games = multiplierGameGenerator.generate(name, multiplierConfig);
        } else {
            throw new IllegalArgumentException(String.format("unknown type '%s'", config.getType()));
        }
        return Baseline.builder()
            .id(ID.gen())
            .name(name)
            .config(config)
            .games(games)
            .createdAt(Instant.now())
            .build();
    }

    private void validateServerParameters(Map<String, String> parameters) throws ValidationException {
        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            parameterValidator.validate(parameter.getKey(), parameter.getValue());
        }
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

}
