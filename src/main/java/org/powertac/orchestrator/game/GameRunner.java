package org.powertac.orchestrator.game;

import java.util.Collection;

public interface GameRunner {

    void run(Game game);
    Collection<Game> getRunningGames();

}
