package org.powertac.rachma.baseline;

import org.apache.commons.lang3.NotImplementedException;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameConfig;
import org.powertac.rachma.game.GameConfigDTO;
import org.powertac.rachma.game.generator.GameGeneratorConfig;
import org.powertac.rachma.game.generator.MultiplierGameGeneratorConfig;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class BaselineDTOMapperImpl implements BaselineDTOMapper {

    @Override
    public BaselineDTO toDTO(Baseline baseline) {
        return BaselineDTO.builder()
            .id(baseline.getId())
            .name(baseline.getName())
            .config(parseConfigDTO(baseline.getConfig()))
            .createdAt(baseline.getCreatedAt().toEpochMilli())
            .gameIds(baseline.getGames().stream().map(Game::getId).collect(Collectors.toList()))
            .build();
    }

    private GameConfigDTO parseConfigDTO(GameGeneratorConfig config) {
        if (config.getType().equals(MultiplierGameGeneratorConfig.TYPE_ID)) {
            GameConfig gameConfig = ((MultiplierGameGeneratorConfig) config).getGameConfig();
            return GameConfigDTO.builder()
                .brokerIds(gameConfig.getBrokers().getIds())
                .parameters(gameConfig.getParameters())
                .weather(gameConfig.getWeather())
                .seed(null)
                .build();
        } else {
            throw new NotImplementedException(String.format(
                "game generator type '%s' is not supported",
                config.getType()));
        }
    }

}
