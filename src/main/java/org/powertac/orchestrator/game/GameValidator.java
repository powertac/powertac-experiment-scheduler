package org.powertac.orchestrator.game;

public interface GameValidator {

    void validate(Game game) throws GameValidationException;

}
