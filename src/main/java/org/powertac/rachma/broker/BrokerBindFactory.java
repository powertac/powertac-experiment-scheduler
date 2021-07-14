package org.powertac.rachma.broker;

import com.github.dockerjava.api.model.Bind;
import org.powertac.rachma.game.Game;

public interface BrokerBindFactory {

    Bind createPropertiesBind(Game game, Broker broker);
    Bind createSharedDirectoryBind(Game game, Broker broker);

}
