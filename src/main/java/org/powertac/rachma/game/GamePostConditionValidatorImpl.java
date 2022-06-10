package org.powertac.rachma.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.docker.DockerContainerExitState;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Component
public class GamePostConditionValidatorImpl implements GamePostConditionValidator {

    private final static int timeslotToDurationFactor = 25;

    private final Logger logger = LogManager.getLogger(GamePostConditionValidator.class);

    @Override
    public boolean isValid(GameRun run, Map<Broker, DockerContainerExitState> exitStates, Instant exitTime) {
        if (!minGameLengthReached(run, exitTime)) {
            logger.error(String.format("game run[id=%s] didn't reach minimum game time", run.getId()));
            return false;
        }
        for (Map.Entry<Broker, DockerContainerExitState> exitState : exitStates.entrySet()) {
            if (exitState.getValue().isErrorState() && !brokerMayFail(exitState.getKey())) {
                logger.error(String.format(
                    "game run[id=%s] - the simulation container '%s' exited with an error code (%d)",
                    run.getId(),
                    exitState.getKey().getName(),
                    exitState.getValue().getExitCode()));
                return false;
            }
        }
        return true;
    }

    private boolean minGameLengthReached(GameRun run, Instant exitTime) {
        int timeslotSeconds = Integer.parseInt(run.getGame().getServerParameters().get("common.competition.simulationTimeslotSeconds"));
        long durationMinutes = Duration.between(run.getStart(), exitTime).abs().toMinutes();
        return ((long) timeslotSeconds * timeslotToDurationFactor) <= durationMinutes;
    }

    private boolean brokerMayFail(Broker broker) {
        return broker.getImageTag().equals("powertac/spot19:latest")
            || broker.getImageTag().equals("powertac/spot17:latest");
    }

}
