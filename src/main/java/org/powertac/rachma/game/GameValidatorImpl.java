package org.powertac.rachma.game;

import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.broker.BrokerTypeRepository;
import org.powertac.rachma.validation.ParameterValidationException;
import org.powertac.rachma.file.File;
import org.powertac.rachma.file.FileRole;
import org.powertac.rachma.validation.SimulationParameterValidator;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class GameValidatorImpl implements GameValidator {

    private final static Pattern namePattern = Pattern.compile("^[a-zA-Z0-9\\-_.\\s]{5,255}$");

    private final GameRepository games;
    private final BrokerTypeRepository brokerTypes;
    private final SimulationParameterValidator parameterValidator;

    public GameValidatorImpl(GameRepository games, BrokerTypeRepository brokerTypes, SimulationParameterValidator parameterValidator) {
        this.games = games;
        this.brokerTypes = brokerTypes;
        this.parameterValidator = parameterValidator;
    }

    @Override
    public void validate(Game game) throws GameValidationException {
        validateName(game.getName());
        validateBrokers(game.getBrokers());
        validateServerParameters(game.getServerParameters());
        validateBootstrap(game.getBootstrap());
        validateSeed(game.getSeed());
    }

    private void validateName(String name) throws GameValidationException {
        if (!namePattern.matcher(name).find()) {
            throw new GameValidationException(String.format("'%s' is not a valid name", name));
        }
    }

    private void validateBrokers(Collection<Broker> brokers) throws GameValidationException {
        if (brokers.size() < 1) {
            throw new GameValidationException("game must have at least one broker assigned");
        }
        for (Broker broker : brokers) {
            if (!brokerTypes.has(broker.getName())) {
                throw new GameValidationException(String.format("broker '%s' does not exist", broker.getName()));
            }
        }
    }

    private void validateServerParameters(Map<String, String> parameters) throws GameValidationException {
        try {
            for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                parameterValidator.validate(parameter.getKey(), parameter.getValue());
            }
        } catch (ParameterValidationException e) {
            throw new GameValidationException("invalid server parameter", e);
        }
    }

    private void validateBootstrap(File bootstrap) throws GameValidationException {
        if (null == bootstrap) {
            return;
        }
        if (!bootstrap.getRole().equals(FileRole.BOOTSTRAP)) {
            throw new GameValidationException(String.format("invalid bootstrap file type '%s'", bootstrap.getRole()));
        }
        if (null == games.findById(bootstrap.getGame().getId())) {
            throw new GameValidationException(String.format(
                "game '%s' referenced in bootstrap file does not exist",
                bootstrap.getGame().getId()));
        }
        // TODO : check for file existence
    }

    private void validateSeed(File seed) throws GameValidationException {
        if (null == seed) {
            return;
        }
        if (!seed.getRole().equals(FileRole.SEED)) {
            throw new GameValidationException(String.format("invalid seed file type '%s'", seed.getRole()));
        }
        if (null == games.findById(seed.getGame().getId())) {
            throw new GameValidationException(String.format(
                "game '%s' referenced in seed file does not exist",
                seed.getGame().getId()));
        }
        // TODO : check for file existence
    }

}
