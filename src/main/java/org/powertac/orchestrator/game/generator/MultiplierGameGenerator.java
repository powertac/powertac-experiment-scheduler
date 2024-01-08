package org.powertac.orchestrator.game.generator;

import org.powertac.orchestrator.game.Game;
import org.powertac.orchestrator.game.GameFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MultiplierGameGenerator implements GameGenerator {

    private final GameFactory gameFactory;

    public MultiplierGameGenerator(GameFactory gameFactory) {
        this.gameFactory = gameFactory;
    }

    @Override
    public List<Game> generate(String name, GameGeneratorConfig config) {
        if (config instanceof MultiplierGameGeneratorConfig) {
            MultiplierGameGeneratorConfig multiplierConfig = (MultiplierGameGeneratorConfig) config;
            List<Game> games = new ArrayList<>();
            for (int i = 0; i < multiplierConfig.getMultiplier(); i++) {
                Game game = gameFactory.createFromConfig(multiplierConfig.getGameConfig());
                game.setName(getName(name, i + 1));
                games.add(game);
            }
            return games;
        } else {
            throw new IllegalArgumentException(String.format(
                "%s cannot process config of type '%s'",
                MultiplierGameGenerator.class,
                config.getType()));
        }
    }

    private String getName(String name, Integer index) {
        return String.format("%s - %d", name, index);
    }

}
