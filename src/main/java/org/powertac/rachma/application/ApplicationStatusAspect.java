package org.powertac.rachma.application;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ApplicationStatusAspect {

    private final ApplicationStatus status;

    public ApplicationStatusAspect(ApplicationStatus status) {
        this.status = status;
    }

    @Before("execution(public void org.powertac.rachma.application.ApplicationSetup.start(..))")
    public void setSettingUp() {
        status.setSettingUp();
    }

    @AfterReturning("execution(public void org.powertac.rachma.application.ApplicationSetup.start(..))")
    public void setRunning() {
        status.setRunning();
    }

}
