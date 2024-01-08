package org.powertac.orchestrator.broker;

import com.github.dockerjava.api.model.Bind;
import org.powertac.orchestrator.game.Game;
import org.powertac.orchestrator.game.GameRun;

public interface BrokerBindFactory {

    Bind createPropertiesBind(Game game, Broker broker);
    Bind createLogDirBind(GameRun run, Broker broker);
    Bind createDataDirBind(GameRun run, Broker broker);

}
