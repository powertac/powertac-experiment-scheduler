package org.powertac.rachma.task;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TaskLifecycleAspect {

    @Before("execution(* org.powertac.rachma.task.TaskRunner+.run(..))")
    public void setRunning(JoinPoint joinPoint) {
        Task task = getTask(joinPoint);
        task.getStatus().setRunning();
    }

    @AfterReturning("execution(* org.powertac.rachma.task.TaskRunner+.run(..))")
    public void setCompleted(JoinPoint joinPoint) {
        Task task = getTask(joinPoint);
        task.getStatus().setCompleted();
    }

    @AfterThrowing(value = "execution(* org.powertac.rachma.task.TaskRunner+.run(..))")
    public void setFailed(JoinPoint joinPoint) {
        Task task = getTask(joinPoint);
        task.getStatus().setFailed();
    }

    private Task getTask(JoinPoint joinPoint) {
        TaskRunner runner = (TaskRunner) joinPoint.getTarget();
        return runner.getTask();
    }

}
