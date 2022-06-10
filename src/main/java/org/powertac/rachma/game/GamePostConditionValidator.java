package org.powertac.rachma.game;

import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.docker.DockerContainerExitState;

import java.time.Instant;
import java.util.Map;

public interface GamePostConditionValidator {

    boolean isValid(GameRun run, Map<Broker, DockerContainerExitState> exitStates, Instant exitTime);

}
