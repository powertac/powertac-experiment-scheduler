package org.powertac.rachma.game;

public interface GameFactory {

    Game createFromSpec(GameSpec spec);

}
