package org.powertac.rachma.broker;

import com.github.dockerjava.api.model.Bind;
import org.powertac.rachma.docker.AbstractBindFactory;
import org.powertac.rachma.game.GameRun;
import org.powertac.rachma.paths.PathProvider;
import org.powertac.rachma.game.Game;
import org.springframework.stereotype.Component;

@Component
public class BrokerBindFactoryImpl extends AbstractBindFactory implements BrokerBindFactory {

    private final PathProvider paths;

    public BrokerBindFactoryImpl(PathProvider paths) {
        this.paths = paths;
    }

    @Override
    public Bind createPropertiesBind(Game game, Broker broker) {
        return bind(
            paths.host().game(game).broker(broker).properties(),
            paths.container().broker(broker).game(game).properties());
    }

    @Override
    public Bind createLogDirBind(GameRun run, Broker broker) {
        return bind(
            paths.host().run(run).broker(broker).dir(),
            paths.container().broker(broker).run(run).logs());
    }

}
