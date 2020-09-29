package org.powertac.rachma.application;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

// TODO : remove or add api endpoint; this is just not useful at the moment
public class ApplicationSetupStatus {

    public enum Step {
        IDLE,
        CREATING_WORKDIR,
        BUILDING_SERVER_IMAGES,
        BUILDING_BROKER_IMAGES
    }

    private Instant start;
    private Instant end;

    @Getter
    @Setter
    private Step currentStep = Step.IDLE;

    public boolean hasStarted() {
        return null != start;
    }

    public boolean isRunning() {
        return null != start
            && null == end;
    }

    public boolean isFinished() {
        return null != start
            && null != end;
    }

}
