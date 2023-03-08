package org.powertac.rachma.game;

import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class GameDTOV2Mapper implements GameDTOMapper {

    @Override
    public GameDTO toDTO(Game game) {
        return GameDTO.builder()
            .id(game.getId())
            .name(game.getName())
            .config(parseConfigDTO(game))
            .createdAt(game.getCreatedAt().toEpochMilli())
            .cancelled(game.isCancelled())
            .runs(game.getRuns().stream().map(this::parseGameRunDTO).collect(Collectors.toList()))
            .baselineId(game.getBaseline() != null ? game.getBaseline().getId() : null)
            .treatmentId(game.getTreatment() != null ? game.getTreatment().getId() : null)
            .baseGameId(game.getBase() != null ? game.getBase().getId() : null)
            .build();
    }

    private GameConfigDTO parseConfigDTO(Game game) {
        return GameConfigDTO.builder()
            .brokerIds(game.getBrokerSet().getIds())
            .parameters(game.getServerParameters())
            .weather(game.getWeatherConfiguration())
            .seed(null)
            .build();
    }

    private GameRunDTO parseGameRunDTO(GameRun run) {
        return GameRunDTO.builder()
            .id(run.getId())
            .start(run.getStart() != null ? run.getStart().toEpochMilli() : null)
            .end(run.getEnd() != null ? run.getEnd().toEpochMilli() : null)
            .phase(run.getPhase().toString())
            .failed(run.hasFailed())
            .build();
    }

}
