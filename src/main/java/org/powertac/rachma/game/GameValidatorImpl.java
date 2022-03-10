package org.powertac.rachma.game;

import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.broker.BrokerRepository;
import org.powertac.rachma.file.File;
import org.powertac.rachma.file.FileRole;
import org.powertac.rachma.paths.PathProvider;
import org.powertac.rachma.validation.ParameterValidationException;
import org.powertac.rachma.validation.SimulationParameterValidator;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class GameValidatorImpl implements GameValidator {

    private final static Pattern namePattern = Pattern.compile("^[a-zA-Z0-9\\-_.\\s]{5,255}$");

    private final GameRepository games;
    private final BrokerRepository brokerRepository;
    private final SimulationParameterValidator parameterValidator;
    private final PathProvider paths;

    public GameValidatorImpl(GameRepository games, BrokerRepository brokerRepository, SimulationParameterValidator parameterValidator, PathProvider paths) {
        this.games = games;
        this.brokerRepository = brokerRepository;
        this.parameterValidator = parameterValidator;
        this.paths = paths;
    }

    @Override
    public void validate(Game game) throws GameValidationException {
        validateName(game.getName());
        validateBrokers(game.getBrokers());
        validateServerParameters(game.getServerParameters());
        if (null != game.getBootstrap()) {
            validateBootstrap(game.getBootstrap());
        }
        if (null != game.getSeed()) {
            validateSeed(game.getSeed());
        }
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
            if (!brokerRepository.exists(broker.getName(), broker.getVersion())) {
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
        if (!bootstrap.getRole().equals(FileRole.BOOTSTRAP)) {
            throw new GameValidationException(String.format("invalid bootstrap file type '%s'", bootstrap.getRole()));
        } else if (null == games.findById(bootstrap.getGame().getId())) {
            throw new GameValidationException(String.format(
                "game '%s' referenced in bootstrap file does not exist",
                bootstrap.getGame().getId()));
        } else if (!Files.exists(paths.local().game(bootstrap.getGame()).bootstrap())) {
            throw new GameValidationException(String.format(
                "bootstrap file '%s' does not exist",
                paths.local().game(bootstrap.getGame()).bootstrap()));
        }
    }

    private void validateSeed(File seed) throws GameValidationException {
        if (!seed.getRole().equals(FileRole.SEED)) {
            throw new GameValidationException(String.format("invalid seed file type '%s'", seed.getRole()));
        } else if (null == games.findById(seed.getGame().getId())) {
            throw new GameValidationException(String.format(
                "game '%s' referenced in seed file does not exist",
                seed.getGame().getId()));
        } else if (!Files.exists(paths.local().run(seed.getGame().getLatestSuccessfulRun()).state())) {
            throw new GameValidationException(String.format(
                "seed file '%s' does not exist",
                paths.local().run(seed.getGame().getLatestSuccessfulRun()).state()));
        }
    }

}
