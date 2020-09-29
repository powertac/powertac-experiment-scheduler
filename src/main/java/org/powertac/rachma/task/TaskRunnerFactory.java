package org.powertac.rachma.task;

import org.powertac.rachma.docker.container.DockerContainerSpec;
import org.powertac.rachma.docker.exception.NotFoundException;
import org.powertac.rachma.powertac.bootstrap.BootstrapTask;
import org.powertac.rachma.powertac.simulation.SimulationTask;
import org.powertac.rachma.runner.ParallelRunnerGroup;
import org.powertac.rachma.runner.Runner;
import org.powertac.rachma.runner.RunnerFactory;
import org.powertac.rachma.runner.SynchronizedParallelRunnerGroup;
import org.powertac.rachma.runner.exception.RunnerCreationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
public class TaskRunnerFactory implements RunnerFactory<Task, Runner> {

    @Value("${container.task.synchronousExecutionGracePeriod}")
    private int synchronousExecutionGracePeriod;

    private final RunnerFactory<DockerContainerSpec, Runner> dockerContainerRunnerFactory;
    private final ContainerSpecificationFactory<BootstrapTask> bootstrapTaskContainerSpecificationFactory;
    private final ContainerSpecificationFactory<SimulationTask> simulationTaskContainerSpecificationFactory;

    @Autowired
    public TaskRunnerFactory(RunnerFactory<DockerContainerSpec, Runner> dockerContainerRunnerFactory,
                             ContainerSpecificationFactory<BootstrapTask> bootstrapTaskContainerSpecificationFactory,
                             ContainerSpecificationFactory<SimulationTask> simulationTaskContainerSpecificationFactory) {
        this.dockerContainerRunnerFactory = dockerContainerRunnerFactory;
        this.bootstrapTaskContainerSpecificationFactory = bootstrapTaskContainerSpecificationFactory;
        this.simulationTaskContainerSpecificationFactory = simulationTaskContainerSpecificationFactory;
    }

    @Override
    public Runner createRunner(Task task) throws RunnerCreationFailedException {
        try {
            Set<DockerContainerSpec> specifications = getContainerSpecs(task);
            return createRunnerGroup(specifications);
        }
        catch (NotFoundException| IOException e) {
            throw new RunnerCreationFailedException(
                String.format("Could not create runner for task of type '%s'", task.getClass().getCanonicalName()));
        }
    }

    private Set<DockerContainerSpec> getContainerSpecs(Task task) throws NotFoundException, IOException, RunnerCreationFailedException {
        if (task instanceof BootstrapTask) {
            return bootstrapTaskContainerSpecificationFactory.createSpecifications((BootstrapTask) task);
        }
        if (task instanceof SimulationTask) {
            return simulationTaskContainerSpecificationFactory.createSpecifications((SimulationTask) task);
        }
        throw new RunnerCreationFailedException(
            String.format("Unsupported task type '%s'", task.getClass().getCanonicalName()));
    }

    private ParallelRunnerGroup createRunnerGroup(Set<DockerContainerSpec> specifications)
        throws RunnerCreationFailedException {
        Set<Runner> runners = createContainerRunners(specifications);
        return new SynchronizedParallelRunnerGroup(runners, synchronousExecutionGracePeriod);
    }

    private Set<Runner> createContainerRunners(Set<DockerContainerSpec> specifications)
        throws RunnerCreationFailedException {
        Set<Runner> runners = new HashSet<>();
        for (DockerContainerSpec container : specifications) {
            Runner runner = dockerContainerRunnerFactory.createRunner(container);
            runners.add(runner);
        }
        return runners;
    }

}
