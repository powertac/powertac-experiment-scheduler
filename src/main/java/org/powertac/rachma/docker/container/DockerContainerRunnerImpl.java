package org.powertac.rachma.docker.container;

import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;
import org.powertac.rachma.docker.exception.ContainerReflectionException;
import org.powertac.rachma.docker.exception.ContainerStartException;
import org.powertac.rachma.docker.exception.KillContainerException;

import java.util.concurrent.TimeUnit;

public class DockerContainerRunnerImpl implements DockerContainerRunner {

    private final DockerContainerController containerController;
    private final DockerContainerRepository containerRepository;
    private final DockerContainer container;
    private final ContainerInspectionConfig inspectionConfig;

    private int inspectionRetryCount = 0;
    private boolean stopSignalReceived = false;

    DockerContainerRunnerImpl(DockerContainerController containerController,
                              DockerContainerRepository containerRepository,
                              DockerContainer container,
                              ContainerInspectionConfig inspectionConfig) {
        this.containerController = containerController;
        this.containerRepository = containerRepository;
        this.inspectionConfig = inspectionConfig;
        this.container = container;
    }

    @Override
    public DockerContainer getContainer() {
        return container;
    }

    @Override
    public void run() throws InterruptedException, ContainerReflectionException, ContainerStartException {
        try {
            start();
            await();
        }
        finally {
            removeContainer();
        }
    }

    @Override
    public void stop() {
        try {
            stopSignalReceived = true;
            containerController.kill(container);
        }
        catch (KillContainerException e) {
            // TODO : log and add to orphaned containers
        }
    }

    private void start() throws ContainerStartException {
        containerController.start(container);
    }

    private void removeContainer() {
        try {
            containerRepository.remove(container);
        }
        catch (NotFoundException e) {
            // container isn't there anymore -> do nothing
        }
        catch (DockerException e) {
            forceRemoveContainer();
        }
    }

    private void forceRemoveContainer() {
        try {
            containerRepository.remove(container, true);
        }
        catch (NotFoundException e) {
            // container isn't there anymore -> do nothing
        }
        catch (DockerException e) {
            // TODO : log possible inconsistency / orphan
        }
    }

    private void await() throws InterruptedException, ContainerReflectionException {
        try {
            while (containerController.isRunning(container)) {
                if (isInterrupted()) {
                    throw new InterruptedException(
                        String.format("execution of container with id=%s was interrupted", container.getId()));
                }
                resetInspectionRetryCount();
                TimeUnit.MILLISECONDS.sleep(inspectionConfig.getInterval());
            }
        }
        catch (NotFoundException e) {
            throw new ContainerReflectionException(
                String.format("container with id=%s has gone missing", container.getId()), e);
        }
        catch (DockerException e) {
            incrementInspectionRetryCount();
            if (inspectionRetryCount > inspectionConfig.getRetryLimit()) {
                throw new ContainerReflectionException(
                    String.format("container reflection failed for container with id=%s", container.getId()), e);
            }
            TimeUnit.MILLISECONDS.sleep(inspectionConfig.getRetryTimeout());
            await();
        }
    }

    private boolean isInterrupted() {
        return stopSignalReceived;
    }

    private void incrementInspectionRetryCount() {
        inspectionRetryCount = inspectionRetryCount + 1;
    }

    private void resetInspectionRetryCount() {
        inspectionRetryCount = 0;
    }

}
