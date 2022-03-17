package org.powertac.rachma.baseline;

import org.apache.commons.lang.StringUtils;
import org.powertac.rachma.broker.BrokerSet;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameFactory;
import org.powertac.rachma.weather.WeatherConfiguration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BaselineGameFactoryImpl implements BaselineGameFactory {

    private final GameFactory gameFactory;
    private final BaselineSpecFactory specFactory;

    public BaselineGameFactoryImpl(GameFactory gameFactory, BaselineSpecFactory specFactory) {
        this.gameFactory = gameFactory;
        this.specFactory = specFactory;
    }

    @Override
    public List<Game> createGames(Baseline baseline) {
        // the exact objects (broker sets, etc.) have to be used here to keep object relationship information
        BaselineSpec spec = BaselineSpec.builder()
            .name(baseline.getName())
            .commonParameters(baseline.getCommonParameters())
            .brokerSets(baseline.getBrokerSets())
            .weatherConfigurations(baseline.getWeatherConfigurations())
            .build();
        return createGames(spec).stream()
            .peek(game -> game.setBaseline(baseline))
            .collect(Collectors.toList());
    }

    @Override
    public List<Game> createGames(BaselineSpec spec) {
        List<Game> games = new ArrayList<>();
        final int baselineSize = spec.getBrokerSets().size() * spec.getWeatherConfigurations().size();
        int index = 0;
        for (BrokerSet brokers : spec.getBrokerSets()) {
            for (WeatherConfiguration weather : spec.getWeatherConfigurations()) {
                index++;
                games.add(gameFactory.createGame(
                    getName(spec.getName(), index, baselineSize),
                    brokers,
                    weather,
                    spec.getCommonParameters(),
                    null));
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
