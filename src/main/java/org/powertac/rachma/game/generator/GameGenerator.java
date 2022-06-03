package org.powertac.rachma.game.generator;

import org.powertac.rachma.game.Game;

import java.util.List;

public interface GameGenerator {

    List<Game> generate(String name, GameGeneratorConfig config);

}
