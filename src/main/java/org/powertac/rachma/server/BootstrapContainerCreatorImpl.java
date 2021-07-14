package org.powertac.rachma.server;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import org.powertac.rachma.docker.container.DockerContainer;
import org.powertac.rachma.file.PathProvider;
import org.powertac.rachma.game.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BootstrapContainerCreatorImpl implements BootstrapContainerCreator {

    @Value("${server.defaultImage}")
    private String defaultImageTag;

    private final DockerClient docker;
    private final ServerContainerCommandCreator commandCreator;
    private final ServerContainerBindFactory bindFactory;
    private final PathProvider paths;

    @Autowired
    public BootstrapContainerCreatorImpl(DockerClient docker, ServerContainerCommandCreator commandCreator, ServerContainerBindFactory bindFactory, PathProvider paths) {
        this.docker = docker;
        this.commandCreator = commandCreator;
        this.bindFactory = bindFactory;
        this.paths = paths;
    }

    @Override
    public DockerContainer create(Game game) throws DockerException {
        CreateContainerCmd create = docker.createContainerCmd(defaultImageTag);
        String name = getName(game);
        create.withName(name);
        create.withCmd(getCommand(game));
        create.withHostConfig(getHostConfig(game));
        CreateContainerResponse response = create.exec();
        return new DockerContainer(response.getId(), name);
    }

    private String getName(Game game) {
        return String.format("boot.%s", game.getId());
    }

    private List<String> getCommand(Game game) {
        return commandCreator.createBootstrapCommand(
            paths.container().game(game).properties().toString(),
            paths.container().game(game).bootstrap().toString());
    }

    private HostConfig getHostConfig(Game game) {
        HostConfig config = new HostConfig();
        config.withBinds(getBinds(game));
        return config;
    }

    private List<Bind> getBinds(Game game) {
        List<Bind> binds = new ArrayList<>();
        binds.add(bindFactory.createSimulationPropertiesBind(game));
        binds.add(bindFactory.createBootstrapBind(game));
        return binds;
    }

}
