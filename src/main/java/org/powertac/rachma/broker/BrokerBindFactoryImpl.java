package org.powertac.rachma.broker;

import com.github.dockerjava.api.model.Bind;
import org.powertac.rachma.docker.AbstractBindFactory;
import org.powertac.rachma.game.GameRun;
import org.powertac.rachma.paths.PathProvider;
import org.powertac.rachma.game.Game;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

@Component
public class BrokerBindFactoryImpl extends AbstractBindFactory implements BrokerBindFactory {

    @Value("${broker.is3.directory.data}")
    private String is3DataDirectory;

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

    @Override
    public Bind createDataDirBind(GameRun run, Broker broker) {
        return bind(
            Paths.get(is3DataDirectory),
            paths.container().broker(broker).run(run).data());
    }
}
