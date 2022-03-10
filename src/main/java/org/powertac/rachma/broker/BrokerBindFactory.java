package org.powertac.rachma.broker;

import com.github.dockerjava.api.model.Bind;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameRun;

public interface BrokerBindFactory {

    Bind createPropertiesBind(Game game, Broker broker);
    Bind createLogDirBind(GameRun run, Broker broker);

}
