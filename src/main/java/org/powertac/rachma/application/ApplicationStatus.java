package org.powertac.rachma.application;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ApplicationStatus {

    public enum State {
        STARTING,
        SETTING_UP,
        RUNNING,
        SHUTTING_DOWN,
        INCONSISTENT
    }

    @Getter
    private List<Throwable> inconsistencies = new ArrayList<>();

    @Getter
    private ApplicationSetupStatus setupStatus = new ApplicationSetupStatus();

    @Getter
    private State state = State.STARTING;

    public void setSettingUp() {
        this.state = State.SETTING_UP;
    }

    public void setRunning() {
        this.state = State.RUNNING;
    }

    public void setShuttingDown() {
        this.state = State.SHUTTING_DOWN;
    }

    public void setInconsistent(Throwable e) {
        this.state = State.INCONSISTENT;
        this.inconsistencies.add(e);
    }

}
