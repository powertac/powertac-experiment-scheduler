package org.powertac.rachma.baseline;

import org.powertac.rachma.broker.BrokerSet;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameFactory;
import org.powertac.rachma.weather.WeatherConfiguration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BaselineGameFactoryImpl implements BaselineGameFactory {

    private final GameFactory gameFactory;

    public BaselineGameFactoryImpl(GameFactory gameFactory) {
        this.gameFactory = gameFactory;
    }

    @Override
    public List<Game> createGames(Baseline baseline) {
        List<Game> games = new ArrayList<>();
        int index = 0;
        for (BrokerSet brokers : baseline.getBrokerSets()) {
            for (WeatherConfiguration weather : baseline.getWeatherConfigurations()) {
                index++;
                games.add(gameFactory.createGame(
                    getName(baseline.getName(), index),
                    brokers,
                    weather,
                    baseline.getCommonParameters(),
                    baseline));
            }
        }
        return games;
    }

    private String getName(String baselineName, int index) {
        return String.format("%s - %d", baselineName, index);
    }

}
