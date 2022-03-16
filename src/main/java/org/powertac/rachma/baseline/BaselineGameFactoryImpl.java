package org.powertac.rachma.baseline;

import org.apache.commons.lang.StringUtils;
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
        final int baselineSize = baseline.getBrokerSets().size() * baseline.getWeatherConfigurations().size();
        int index = 0;
        for (BrokerSet brokers : baseline.getBrokerSets()) {
            for (WeatherConfiguration weather : baseline.getWeatherConfigurations()) {
                index++;
                games.add(gameFactory.createGame(
                    getName(baseline.getName(), index, baselineSize),
                    brokers,
                    weather,
                    baseline.getCommonParameters(),
                    baseline));
            }
        }
        return games;
    }

    private String getName(String baselineName, int index, int baselineSize) {
        final int maxDigits = (int) (Math.log10(baselineSize) + 1);
        return String.format("%s - %s",
            baselineName,
            StringUtils.leftPad(String.valueOf(index), maxDigits, "0"));
    }

}
