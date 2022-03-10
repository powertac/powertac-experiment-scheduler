package org.powertac.rachma.server;

import com.github.dockerjava.api.model.Bind;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameRun;

public interface ServerContainerBindFactory {

    Bind createSimulationPropertiesBind(Game game);
    Bind createBootstrapBind(Game game);
    Bind createSeedBind(Game game);
    Bind createStateLogBind(GameRun run);
    Bind createTraceLogBind(GameRun run);

}
