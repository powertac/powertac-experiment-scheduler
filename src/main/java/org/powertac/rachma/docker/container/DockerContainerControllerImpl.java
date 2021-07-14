package org.powertac.rachma.docker.container;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import org.powertac.rachma.docker.exception.ContainerException;
import org.powertac.rachma.docker.exception.ContainerReflectionException;
import org.powertac.rachma.docker.exception.ContainerStartException;
import org.powertac.rachma.docker.exception.KillContainerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class DockerContainerControllerImpl implements DockerContainerController {

    private final static int runPoolSize = 10;
    private final static int pollingIntervalMillis = 1000;

    private final DockerClient dockerClient;
    private final ExecutorService runPool;

    @Autowired
    public DockerContainerControllerImpl(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        runPool = Executors.newFixedThreadPool(runPoolSize);
    }

    @Override
    public void start(DockerContainer container) throws ContainerStartException {
        try {
            dockerClient.startContainerCmd(container.getId()).exec();
        }
        catch (DockerException e) {
            throw new ContainerStartException("could not start container with name=" + container.getName());
        }
    }

    @Override
    public void kill(DockerContainer container) throws KillContainerException {
        try {
            dockerClient.killContainerCmd(container.getId()).exec();
        }
        catch (DockerException e) {
            // TODO : kill should always return without exception; should an exception occur it should be logged and
            //        the respective container should be added to the list of orphaned resources
            throw new KillContainerException("could not kill container with name=" + container.getName());
        }
    }

    @Override
    public ContainerExitState run(DockerContainer container) throws ContainerException {
        try {
            Future<ContainerExitState> exitFuture = runPool.submit(createSynchronousRun(container));
            return exitFuture.get();
        } catch (Exception e) {
            // TODO : check if container should be kept or any run data should be persisted
            kill(container);
            throw new ContainerException("container run failed", e);
        }
    }

    @Override
    public Map<DockerContainer, ContainerExitState> run(Set<DockerContainer> containers) throws ContainerException {
        Map<Future<ContainerExitState>, DockerContainer> exitFutures = new HashMap<>();
        CompletionService<ContainerExitState> completion = new ExecutorCompletionService<>(runPool);
        // create and start synchronous runs
        for (DockerContainer container : containers) {
            exitFutures.put(completion.submit(createSynchronousRun(container)), container);
        }
        try {
            boolean completed = false;
            Map<DockerContainer, ContainerExitState> exitStates = new HashMap<>();
            // wait for all runs (futures) to complete
            while (!completed) {
                Future<ContainerExitState> future = completion.take();
                exitStates.put(exitFutures.get(future), future.get());
                completed = exitStates.size() == exitFutures.size();
            }
            return exitStates;
        } catch (InterruptedException|ExecutionException e) {
            // kill all containers if an error occurs
            for (DockerContainer container : containers) {
                kill(container);
            }
            throw new ContainerException("container run failed", e);
        }
    }

    @Override
    public boolean isRunning(DockerContainer container) throws ContainerReflectionException {
        return isRunningState(getState(container));
    }

    private Callable<ContainerExitState> createSynchronousRun(DockerContainer container) {
        return () -> {
            start(container);
            while (true) {
                InspectContainerResponse.ContainerState state = getState(container);
                if (!isRunningState(state)) {
                    return new ContainerExitState(getExitCode(state));
                }
                Thread.sleep(pollingIntervalMillis);
            }
        };
    }

    private InspectContainerResponse.ContainerState getState(DockerContainer container) throws ContainerReflectionException {
        try {
            InspectContainerResponse inspection = dockerClient.inspectContainerCmd(container.getId()).exec();
            InspectContainerResponse.ContainerState state = inspection.getState();
            if (null == state) {
                throw new ContainerReflectionException("failed to get state of container with id=" + container.getId());
            }
            return state;
        } catch (DockerException e) {
            throw new ContainerReflectionException("failed to get inspection response for container with id=" + container.getId(), e);
        }
    }

    private boolean isRunningState(InspectContainerResponse.ContainerState state) throws ContainerReflectionException {
        Boolean isRunning = state.getRunning();
        if (null == isRunning) {
            throw new ContainerReflectionException("failed to get container run state");
        }
        return isRunning;
    }

    private int getExitCode(InspectContainerResponse.ContainerState state) throws ContainerReflectionException {
        Long exitCode = state.getExitCodeLong();
        if (exitCode == null) {
            throw new ContainerReflectionException("failed to get container exit state");
        }
        return exitCode.intValue();
    }

}
