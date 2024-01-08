package org.powertac.orchestrator.persistence;

public interface SeederManager {

    void runSeeders() throws SeederException;

}
