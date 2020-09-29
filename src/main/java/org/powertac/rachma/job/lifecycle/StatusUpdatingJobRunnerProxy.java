package org.powertac.rachma.job.lifecycle;

import org.powertac.rachma.job.Job;
import org.powertac.rachma.job.JobRepository;
import org.powertac.rachma.job.JobRunner;
import org.powertac.rachma.job.JobStatus;

import java.util.function.Consumer;

public class StatusUpdatingJobRunnerProxy implements JobRunner {

    private final JobRunner runner;
    private final JobRepository jobRepository;

    public StatusUpdatingJobRunnerProxy(JobRunner runner, JobRepository jobRepository) {
        this.runner = runner;
        this.jobRepository = jobRepository;
    }

    @Override
    public Job getJob() {
        return runner.getJob();
    }

    @Override
    public void run() throws Exception {
        try {
            updateStatus(getJob(), JobStatus::setRunning);
            runner.run();
            updateStatus(getJob(), JobStatus::setCompleted);
        }
        catch (Exception e) {
            updateStatus(getJob(), JobStatus::setFailed);
            throw e;
        }
    }

    @Override
    public void stop() {
        runner.stop();
        updateStatus(getJob(), JobStatus::setFailed);
    }

    private void updateStatus(Job job, Consumer<JobStatus> statusChanger) {
        statusChanger.accept(job.getStatus());
        jobRepository.update(job);
    }

}
