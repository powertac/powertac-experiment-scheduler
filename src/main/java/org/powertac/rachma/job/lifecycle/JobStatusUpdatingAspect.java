package org.powertac.rachma.job.lifecycle;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.powertac.rachma.job.Job;
import org.powertac.rachma.job.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Deprecated
public class JobStatusUpdatingAspect {

    private final JobRepository jobRepository;

    @Autowired
    public JobStatusUpdatingAspect(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // TODO : make this explicit! (move to job scheduler)
    @AfterReturning("execution(* org.powertac.rachma.job.JobScheduler+.schedule(org.powertac.rachma.job.Job+))")
    public void setQueued(JoinPoint joinPoint) {
        Job job = (Job) joinPoint.getArgs()[0];
        job.getStatus().setQueued();
        jobRepository.update(job);
    }

}
