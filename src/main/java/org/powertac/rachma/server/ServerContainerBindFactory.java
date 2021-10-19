package org.powertac.rachma.server;

import com.github.dockerjava.api.model.Bind;
import org.powertac.rachma.game.Game;

public interface ServerContainerBindFactory {

    Bind createSimulationPropertiesBind(Game game);
    Bind createLogDirBind(Game game);
    Bind createBootstrapBind(Game game);
    Bind createSeedBind(Game game);
    Bind createStateLogBind(Game game);
    Bind createTraceLogBind(Game game);

}
