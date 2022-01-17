package org.powertac.rachma.game;

import org.powertac.rachma.baseline.Baseline;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.broker.BrokerSet;
import org.powertac.rachma.file.File;
import org.powertac.rachma.file.FileRole;
import org.powertac.rachma.weather.WeatherConfiguration;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class GameFactoryImpl implements GameFactory {

    private final GameRepository gameRepository;

    public GameFactoryImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public Game createFromSpec(GameSpec spec) {
        return new Game(
            UUID.randomUUID().toString(),
            spec.getName(),
            createBrokerSet(spec.getBrokers()),
            spec.getServerParameters(),
            getFile(spec.getBaseGameId(), FileRole.BOOTSTRAP),
            getFile(spec.getBaseGameId(), FileRole.SEED),
            Instant.now());
    }

    @Override
    public Game createGame(String name, BrokerSet brokers, WeatherConfiguration weather, Map<String, String> parameters, Baseline baseline) {
        return new Game(
            UUID.randomUUID().toString(),
            name,
            brokers,
            parameters,
            null,
            null,
            Instant.now(),
            new ArrayList<>(),
            false,
            weather,
            baseline);
    }

    // TODO : use file repository
    // TODO : should throw not found exception
    private File getFile(String baseGameId, FileRole role) {
        if (null == baseGameId) {
            return null;
        }
        Game baseGame = gameRepository.findById(baseGameId);
        return new File(UUID.randomUUID().toString(), role, baseGame);
    }

    // TODO : use broker set repo
    private BrokerSet createBrokerSet(Set<Broker> brokers) {
        return new BrokerSet(
            UUID.randomUUID().toString(),
            brokers);
    }

}
