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
        scheduleQueuedJobs();
        startScheduler();
    }

    private void scheduleQueuedJobs() {
        for (Job job : jobRepository.findAllQueuedJobs()) {
            try {
                scheduler.schedule(job);
            }
            catch (JobSchedulingException e) {
                // TODO : log inability to schedule job
            }
        }
    }

    private void startScheduler() {
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(scheduler::runNextJob, 0, 1, TimeUnit.SECONDS);
    }

}
