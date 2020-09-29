package org.powertac.rachma.job;

import org.powertac.rachma.job.lifecycle.StatusUpdatingJobRunnerProxy;
import org.powertac.rachma.powertac.simulation.SimulationJob;
import org.powertac.rachma.runner.RunnerFactory;
import org.powertac.rachma.runner.exception.RunnerCreationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DelegatingJobRunnerFactory implements RunnerFactory<Job, JobRunner> {

    private final RunnerFactory<SimulationJob, JobRunner> simulationJobRunnerFactory;
    private final JobRepository jobRepository;

    @Autowired
    public DelegatingJobRunnerFactory(RunnerFactory<SimulationJob, JobRunner> simulationJobRunnerFactory, JobRepository jobRepository) {
        this.simulationJobRunnerFactory = simulationJobRunnerFactory;
        this.jobRepository = jobRepository;
    }

    @Override
    public JobRunner createRunner(Job job) throws RunnerCreationFailedException {
        if (isSimulationJob(job)) {
            JobRunner simulationJobRunner = simulationJobRunnerFactory.createRunner((SimulationJob) job);
            return getStatusUpdatingProxy(simulationJobRunner);
        }
        throw new RunnerCreationFailedException(
            String.format("%s is not supported", job.getClass().getCanonicalName()));
    }

    private boolean isSimulationJob(Job job) {
        return job instanceof SimulationJob;
    }

    private JobRunner getStatusUpdatingProxy(JobRunner runner) {
        return new StatusUpdatingJobRunnerProxy(runner, jobRepository);
    }

}
