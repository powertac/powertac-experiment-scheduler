package org.powertac.rachma.game;

import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.broker.BrokerRepository;
import org.powertac.rachma.broker.BrokerSpec;
import org.powertac.rachma.file.File;
import org.powertac.rachma.file.FileRole;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class GameFactoryImpl implements GameFactory {

    private final BrokerRepository brokerRepository;
    private final GameRepository gameRepository;

    public GameFactoryImpl(BrokerRepository brokerRepository, GameRepository gameRepository) {
        this.brokerRepository = brokerRepository;
        this.gameRepository = gameRepository;
    }

    @Override
    public Game createFromSpec(GameSpec spec) {
        return new Game(
            UUID.randomUUID().toString(),
            spec.getName(),
            getBrokers(spec.getBrokers()),
            spec.getServerParameters(),
            getFile(spec.getBaseGameId(), FileRole.BOOTSTRAP),
            getFile(spec.getBaseGameId(), FileRole.SEED),
            Instant.now(),
            new ArrayList<>());
    }

    // TODO : should throw not found exception
    private Set<Broker> getBrokers(Set<BrokerSpec> brokerSpecs) {
        Set<Broker> brokers = new HashSet<>();
        for (BrokerSpec spec : brokerSpecs) {
            brokers.add(brokerRepository.findByName(spec.getName()));
        }
        return brokers;
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



}
