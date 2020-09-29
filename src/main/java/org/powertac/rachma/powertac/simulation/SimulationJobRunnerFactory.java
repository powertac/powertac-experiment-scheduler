package org.powertac.rachma.powertac.simulation;

import org.powertac.rachma.job.JobRunner;
import org.powertac.rachma.job.SerialJobRunnerGroup;
import org.powertac.rachma.runner.Runner;
import org.powertac.rachma.runner.RunnerFactory;
import org.powertac.rachma.runner.exception.RunnerCreationFailedException;
import org.powertac.rachma.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SimulationJobRunnerFactory implements RunnerFactory<SimulationJob, JobRunner> {

    private final RunnerFactory<Task, Runner> taskRunnerFactory;

    @Autowired
    public SimulationJobRunnerFactory(RunnerFactory<Task, Runner> taskRunnerFactory) {
        this.taskRunnerFactory = taskRunnerFactory;
    }

    @Override
    public JobRunner createRunner(SimulationJob job) throws RunnerCreationFailedException {
        List<Runner> orderedTaskRunners = getOrderedTaskRunners(job);
        return new SerialJobRunnerGroup(job, orderedTaskRunners);
    }

    private List<Runner> getOrderedTaskRunners(SimulationJob job) throws RunnerCreationFailedException {
        List<Runner> orderedRunners = new ArrayList<>();
        orderedRunners.add(0, createBootstrapTaskRunner(job));
        orderedRunners.add(1, createSimulationTaskRunner(job));
        return orderedRunners;
    }

    private Runner createBootstrapTaskRunner(SimulationJob job) throws RunnerCreationFailedException {
        return taskRunnerFactory.createRunner(job.getBootstrapTask());
    }

    private Runner createSimulationTaskRunner(SimulationJob job) throws RunnerCreationFailedException {
        return taskRunnerFactory.createRunner(job.getSimulationTask());
    }

}
