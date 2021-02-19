package org.powertac.rachma.job;

import org.powertac.rachma.job.exception.JobSchedulingException;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class JobSchedulerInitializer {

    private final JobScheduler scheduler;
    private final JobRepository jobRepository;

    public JobSchedulerInitializer(JobScheduler scheduler, JobRepository jobRepository) {
        this.scheduler = scheduler;
        this.jobRepository = jobRepository;
    }

    public void initialize() {
        updateFailedJobs();
        scheduleQueuedJobs();
        startScheduler();
    }

    private void updateFailedJobs() {
        for (Job job : jobRepository.findRunningJobs()) {
            // TODO : this is just a workaround that assumes that all jobs that were still running at the time the
            //        orchestrator shut down are assumed to have failed; Update in version 0.1.2 with new execution
            //        model
            job.getStatus().setFailed();
            jobRepository.update(job);
        }
    }

    private void scheduleQueuedJobs() {
        for (Job job : jobRepository.findQueuedJobs()) {
            try {
                scheduler.schedule(job);
            }
            catch (JobSchedulingException e) {
                // TODO : log inability to schedule job
            }
        }
    }

    private void startScheduler() {
        // TODO : refactor ... this uses implicit behaviour (tasks being delayed until thread becomes free)
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(scheduler::runNextJob, 0, 1, TimeUnit.SECONDS);
    }

}
