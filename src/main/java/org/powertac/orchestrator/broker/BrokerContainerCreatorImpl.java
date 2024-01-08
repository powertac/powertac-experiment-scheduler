package org.powertac.orchestrator.broker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import org.powertac.orchestrator.docker.DockerContainer;
import org.powertac.orchestrator.docker.DockerNetwork;
import org.powertac.orchestrator.game.GameRun;
import org.powertac.orchestrator.paths.PathProvider;
import org.powertac.orchestrator.game.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class BrokerContainerCreatorImpl implements BrokerContainerCreator {

    private final DockerClient docker;
    private final PathProvider paths;
    private final BrokerBindFactory bindFactory;

    @Autowired
    public BrokerContainerCreatorImpl(DockerClient docker, PathProvider paths, BrokerBindFactory bindFactory) {
        this.docker = docker;
        this.paths = paths;
        this.bindFactory = bindFactory;
    }

    @Override
    public DockerContainer create(GameRun run, Broker broker, DockerNetwork network) throws DockerException {
        CreateContainerCmd create = docker.createContainerCmd(broker.getImageTag());
        String name = getBrokerContainerName(run, broker);
        create.withName(name);
        create.withCmd(getCommand(run.getGame(), broker));
        create.withHostConfig(getHostConfig(run, broker, network));
        CreateContainerResponse response = create.exec();
        return new DockerContainer(response.getId(), name);
    }

    @Override
    public String getBrokerContainerName(GameRun run, Broker broker) {
        return String.format("%s..%s", broker.getHumanReadableIdentifier(), run.getId());
    }

    private List<String> getCommand(Game game, Broker broker) {
        Path propertiesContainerPath = paths.container().broker(broker).game(game).properties();
        List<String> command = new ArrayList<>();
        command.add("--config");
        command.add(propertiesContainerPath.toString());
        return command;
    }

    private HostConfig getHostConfig(GameRun run, Broker broker, DockerNetwork network) {
        HostConfig config = new HostConfig();
        config.withBinds(getBinds(run, broker));
        config.withNetworkMode(network.getId());
        return config;
    }

    private List<Bind> getBinds(GameRun run, Broker broker) {
        List<Bind> binds = new ArrayList<>();
        binds.add(bindFactory.createPropertiesBind(run.getGame(), broker));
        binds.add(bindFactory.createLogDirBind(run, broker));
        if (broker.getName().toLowerCase().contains("is3")) {
            binds.add(bindFactory.createDataDirBind(run, broker));
        }
        return binds;
    }

}

