package org.powertac.rachma.server;

import com.github.dockerjava.api.model.Bind;
import org.powertac.rachma.docker.AbstractBindFactory;
import org.powertac.rachma.file.PathProvider;
import org.powertac.rachma.game.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServerContainerBindFactoryImpl extends AbstractBindFactory implements ServerContainerBindFactory {

    private final PathProvider paths;

    @Autowired
    public ServerContainerBindFactoryImpl(PathProvider paths) {
        this.paths = paths;
    }

    @Override
    public Bind createSimulationPropertiesBind(Game game) {
        return bind(
            paths.host().game(game).properties(),
            paths.container().game(game).properties());
    }

    @Override
    public Bind createLogDirBind(Game game) {
        return bind(
            paths.host().game(game).logs(),
            paths.container().game(game).logs());
    }

    @Override
    public Bind createBootstrapBind(Game game) {
        return bind(
            paths.host().game(game).bootstrap(),
            paths.container().game(game).bootstrap());
    }

    @Override
    public Bind createSeedBind(Game game) {
        return bind(
            paths.host().game(game).seed(),
            paths.container().game(game).seed());
    }

}
