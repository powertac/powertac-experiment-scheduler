package org.powertac.orchestrator.game;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
public class GameDTO {

    @Getter
    private String id;

    @Getter
    private String name;

    @Getter
    private GameConfigDTO config;

    @Getter
    private Long createdAt;

    @Getter
    private Boolean cancelled;

    @Getter
    private List<GameRunDTO> runs;

    @Getter
    private String baselineId;

    @Getter
    private String treatmentId;

    @Getter
    private String baseGameId;

}
