package org.powertac.orchestrator.baseline;

import lombok.Builder;
import lombok.Getter;
import org.powertac.orchestrator.game.GameConfigDTO;

import java.util.List;

@Builder
public class BaselineDTO {

    @Getter
    private String id;

    @Getter
    private String name;

    @Getter
    private GameConfigDTO config;

    @Getter
    private Long createdAt;

    @Getter
    private List<String> gameIds;

}
