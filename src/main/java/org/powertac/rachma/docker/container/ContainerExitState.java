package org.powertac.rachma.docker.container;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ContainerExitState {

    @Getter
    private final int exitCode;

    public boolean isErrorState() {
        return exitCode != 0;
    }

}
