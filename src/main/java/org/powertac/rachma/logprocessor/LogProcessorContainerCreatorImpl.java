package org.powertac.rachma.logprocessor;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import org.powertac.rachma.docker.ContainerCreator;
import org.powertac.rachma.docker.DockerContainer;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameRun;
import org.powertac.rachma.paths.PathProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LogProcessorContainerCreatorImpl implements ContainerCreator<LogProcessorTask> {

    private final static String containerRootPath = "/opt/powertac/log-processor";

    @Value("${logprocessor.container.defaultImage}")
    private String defaultImageTag;

    private final DockerClient docker;
    private final PathProvider paths;

    public LogProcessorContainerCreatorImpl(DockerClient docker, PathProvider paths) {
        this.docker = docker;
        this.paths = paths;
    }

    @Override
    public DockerContainer createFor(LogProcessorTask task) {
        String containerName = getContainerName(task.getGame().getId());
        String containerId = docker.createContainerCmd(defaultImageTag)
            .withName(containerName)
            .withHostConfig(getHostConfig(task.getGame()))
            .withCmd(getCommand(task.getGame()))
            .exec().getId();
        return new DockerContainer(containerId, containerName);
    }

    private String getContainerName(String gameId) {
        return String.format("logprocessor.%s", gameId);
    }

    private HostConfig getHostConfig(Game game) {
        GameRun run = game.getLatestSuccessfulRun();
        return new HostConfig()
            .withBinds(
                new Bind(
                    paths.host().run(run).state().toString(),
                    new Volume(containerRootPath + "/game.state")),
                new Bind(
                    paths.host().game(game).artifacts().toString(),
                    new Volume(containerRootPath + "/artifacts")));
    }

    private List<String> getCommand(Game game) {
        List<String> command = new ArrayList<>();
        command.add("game.state");
        command.add("--game");
        command.add(game.getId());
        command.add("--out");
        command.add("artifacts");
        return command;
    }

}
