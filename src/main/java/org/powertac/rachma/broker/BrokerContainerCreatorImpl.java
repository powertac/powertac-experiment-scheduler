package org.powertac.rachma.broker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import org.powertac.rachma.docker.container.DockerContainer;
import org.powertac.rachma.docker.network.DockerNetwork;
import org.powertac.rachma.file.PathProvider;
import org.powertac.rachma.game.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class BrokerContainerCreatorImpl implements BrokerContainerCreator {

    private final DockerClient docker;
    private final BrokerImageResolver imageResolver;
    private final PathProvider paths;
    private final BrokerBindFactory bindFactory;

    @Autowired
    public BrokerContainerCreatorImpl(DockerClient docker, BrokerImageResolver imageResolver, PathProvider paths,
                                      BrokerBindFactory bindFactory) {
        this.docker = docker;
        this.imageResolver = imageResolver;
        this.paths = paths;
        this.bindFactory = bindFactory;
    }

    @Override
    public DockerContainer create(Game game, Broker broker, DockerNetwork network) throws DockerException {
        CreateContainerCmd create = docker.createContainerCmd(imageResolver.getImageTag(broker));
        String name = getName(game, broker);
        create.withName(name);
        create.withCmd(getCommand(game, broker));
        create.withHostConfig(getHostConfig(game, broker, network));
        CreateContainerResponse response = create.exec();
        return new DockerContainer(response.getId(), name);
    }

    private String getName(Game game, Broker broker) {
        return String.format("%s.%s", broker.getName(), game.getId());
    }

    private List<String> getCommand(Game game, Broker broker) {
        Path propertiesContainerPath = paths.container().game(game).broker(broker).properties();
        List<String> command = new ArrayList<>();
        command.add("--config");
        command.add(propertiesContainerPath.toString());
        return command;
    }

    private HostConfig getHostConfig(Game game, Broker broker, DockerNetwork network) {
        HostConfig config = new HostConfig();
        config.withBinds(getBinds(game, broker));
        config.withNetworkMode(network.getId());
        return config;
    }

    private List<Bind> getBinds(Game game, Broker broker) {
        List<Bind> binds = new ArrayList<>();
        binds.add(bindFactory.createPropertiesBind(game, broker));
        binds.add(bindFactory.createSharedDirectoryBind(game, broker));
        return binds;
    }

}

