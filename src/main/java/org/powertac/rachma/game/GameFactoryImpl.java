package org.powertac.rachma.game;

import org.powertac.rachma.baseline.Baseline;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.broker.BrokerSet;
import org.powertac.rachma.file.File;
import org.powertac.rachma.file.FileRole;
import org.powertac.rachma.util.ID;
import org.powertac.rachma.weather.WeatherConfiguration;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashSet;
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
            createFile(spec.getBaseGameId(), FileRole.BOOTSTRAP),
            createFile(spec.getBaseGameId(), FileRole.SEED),
            Instant.now());
    }

    @Override
    public Game createGame(String name, BrokerSet brokers, WeatherConfiguration weather, Map<String, String> parameters, Baseline baseline) {
        return Game.builder()
            .id(UUID.randomUUID().toString())
            .name(name)
            .brokerSet(brokers)
            .serverParameters(parameters)
            .createdAt(Instant.now())
            .weatherConfiguration(weather)
            .baseline(baseline)
            .build();
    }

    // TODO : use file repository
    // TODO : should throw not found exception
    private File createFile(String baseGameId, FileRole role) {
        if (null == baseGameId) {
            return null;
        }
        Game baseGame = gameRepository.findById(baseGameId);
        return new File(ID.gen(), role, baseGame);
    }

    // TODO : use broker set repo
    private BrokerSet createBrokerSet(Set<Broker> brokers) {
        return new BrokerSet(
            UUID.randomUUID().toString(),
            new HashSet<>(brokers));
    }

    private WeatherConfiguration copyWeatherConfig(WeatherConfiguration config) {
        return new WeatherConfiguration(config.getLocation(), config.getStartTime());
    }

}
