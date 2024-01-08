package org.powertac.orchestrator.game.generator;

import org.powertac.orchestrator.game.Game;

import java.util.List;

public interface GameGenerator {

    List<Game> generate(String name, GameGeneratorConfig config);

}
