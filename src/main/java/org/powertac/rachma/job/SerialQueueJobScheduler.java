package org.powertac.rachma.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.runner.Runner;
import org.powertac.rachma.runner.RunnerFactory;
import org.powertac.rachma.job.exception.JobSchedulingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

@Service
public class SerialQueueJobScheduler implements JobScheduler {

    private final RunnerFactory<Job, JobRunner> runnerFactory;
    private final JobRepository jobRepository;
    private final Queue<Job> jobQueue = new LinkedList<>();
    private final Logger logger;
    private Runner currentJobRunner;

    @Autowired
    public SerialQueueJobScheduler(RunnerFactory<Job, JobRunner> runnerFactory, JobRepository jobRepository) {
        this.runnerFactory = runnerFactory;
        this.jobRepository = jobRepository;
        this.logger = LogManager.getLogger(JobScheduler.class);
    }

    @Override
    public void runNextJob() {
        if (hasJobQueued()) {
            runNextJobInQueue();
        }
    }

    @Override
    public void schedule(Job job) throws JobSchedulingException {
        if (!jobQueue.offer(job)) {
            throw new JobSchedulingException("cannot schedule job '" + job.getName() + "'");
        }
    }

    @Override
    public void schedule(Collection<Job> jobs) throws JobSchedulingException {
        for (Job job : jobs) {
            schedule(job);
            job.getStatus().setQueued();
            jobRepository.update(job);
        }
    }

    @PreDestroy
    public void cleanup() {
        jobQueue.clear();
        if (null != currentJobRunner) {
            currentJobRunner.stop();
        }
    }

    private boolean hasJobQueued() {
        return null != jobQueue.peek();
    }

    private void runNextJobInQueue() {
        try {
            Job currentJob = jobQueue.poll();
            currentJobRunner = runnerFactory.createRunner(currentJob);
            currentJobRunner.run();
        }
        catch (Exception e) {
            logger.error("An error occurred during job execution", e);
            currentJobRunner.stop();
        }
        finally {
            currentJobRunner = null;
        }
    }

}
