package org.powertac.rachma.server;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.InternetProtocol;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.docker.container.DockerContainer;
import org.powertac.rachma.docker.network.DockerNetwork;
import org.powertac.rachma.file.PathProvider;
import org.powertac.rachma.game.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SimulationContainerCreatorImpl implements SimulationContainerCreator {

    private final static String serverAlias = "powertac-server";

    @Value("${server.defaultImage}")
    private String defaultImageTag;

    @Value("${simulation.container.defaultMessageBrokerPort}")
    private int defaultMessageBrokerPort;

    private final DockerClient docker;
    private final ServerContainerCommandCreator commandCreator;
    private final ServerContainerBindFactory bindFactory;
    private final PathProvider paths;

    @Autowired
    public SimulationContainerCreatorImpl(DockerClient docker, ServerContainerCommandCreator commandCreator, ServerContainerBindFactory bindFactory, PathProvider paths) {
        this.docker = docker;
        this.commandCreator = commandCreator;
        this.bindFactory = bindFactory;
        this.paths = paths;
    }

    public DockerContainer create(Game game, DockerNetwork network) throws DockerException {
        CreateContainerCmd create = docker.createContainerCmd(defaultImageTag);
        String name = getName(game);
        create.withName(name);
        create.withCmd(getCommand(game));
        create.withExposedPorts(new ExposedPort(defaultMessageBrokerPort, InternetProtocol.TCP));
        create.withAliases(serverAlias);
        create.withHostConfig(getHostConfig(game, network));
        CreateContainerResponse response = create.exec();
        return new DockerContainer(response.getId(), name);
    }

    private String getName(Game game) {
        return String.format("sim.%s", game.getId());
    }

    private List<String> getCommand(Game game) {
        Set<String> brokerNames = game.getBrokers().stream()
            .map(Broker::getName)
            .collect(Collectors.toSet());
        Path seedFilePath = useSeed(game) ? paths.container().game(game).seed() : null;
        return commandCreator.createSimulationCommand(
            paths.container().game(game).properties().toString(),
            paths.container().game(game).bootstrap().toString(),
            seedFilePath != null ? seedFilePath.toString() : null,
            brokerNames);
    }

    private HostConfig getHostConfig(Game game, DockerNetwork network) {
        HostConfig config = new HostConfig();
        config.withBinds(getBinds(game));
        config.withNetworkMode(network.getId());
        return config;
    }

    private List<Bind> getBinds(Game game) {
        List<Bind> binds = new ArrayList<>();
        binds.add(bindFactory.createSimulationPropertiesBind(game));
        binds.add(bindFactory.createBootstrapBind(game));
        binds.add(bindFactory.createLogDirBind(game));
        if (useSeed(game)) {
            binds.add(bindFactory.createSeedBind(game));
        }
        return binds;
    }

    private boolean useSeed(Game game) {
        return null != game.getSeed();
    }

}
