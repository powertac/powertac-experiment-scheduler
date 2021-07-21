package org.powertac.rachma.api.stomp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.powertac.rachma.job.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class JobChangeMessageAspect {

    private final SimpMessagingTemplate template;
    private final Logger logger;

    @Autowired
    public JobChangeMessageAspect(SimpMessagingTemplate template) {
        this.template = template;
        this.logger = LogManager.getLogger(JobChangeMessageAspect.class);
    }

    @AfterReturning("execution(public void org.powertac.rachma.job.JobRepository+.create(..))")
    public void pushCreate(JoinPoint joinPoint) {
        pushChangedJob((Job) joinPoint.getArgs()[0]);
    }

    @AfterReturning("execution(public void org.powertac.rachma.job.JobRepository+.update(..))")
    public void pushUpdate(JoinPoint joinPoint) {
        pushChangedJob((Job) joinPoint.getArgs()[0]);
    }

    @AfterReturning("execution(public void org.powertac.rachma.job.JobRepository+.remove(..))")
    public void pushRemove(JoinPoint joinPoint) {
        pushChangedJob((Job) joinPoint.getArgs()[0]);
    }

    private void pushChangedJob(Job job) {
        try {
            template.convertAndSend("/jobs", job);
        } catch (MessagingException e) {
            logger.error("could not send job update for job with id=" + job.getId(), e);
        }
    }

}
