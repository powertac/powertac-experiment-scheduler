package org.powertac.orchestrator.game;

import org.powertac.orchestrator.broker.Broker;
import org.powertac.orchestrator.docker.DockerContainerExitState;

import java.time.Instant;
import java.util.Map;

public interface GamePostConditionValidator {

    boolean isValid(GameRun run, Map<Broker, DockerContainerExitState> exitStates, Instant exitTime);

}
