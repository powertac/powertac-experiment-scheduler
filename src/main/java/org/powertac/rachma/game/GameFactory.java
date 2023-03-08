package org.powertac.rachma.game;

import org.powertac.rachma.baseline.Baseline;
import org.powertac.rachma.broker.BrokerNotFoundException;
import org.powertac.rachma.broker.BrokerSet;
import org.powertac.rachma.weather.WeatherConfiguration;

import java.util.Map;

public interface GameFactory {

    Game createFromDTO(NewGameDTO newGameData) throws BrokerNotFoundException;
    Game createFromSpec(GameSpec spec);
    Game createGame(String name, BrokerSet brokers, WeatherConfiguration weather, Map<String, String> parameters, Baseline baseline);
    Game createFromConfig(GameConfig config);

}
