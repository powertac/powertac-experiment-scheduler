package org.powertac.orchestrator.treatment;

import lombok.Builder;
import lombok.Getter;
import org.powertac.orchestrator.game.GameConfigDTO;

import java.util.List;

@Builder
public class TreatmentDTO {

    @Getter
    private String id;

    @Getter
    private String name;

    @Getter
    private String baselineId;

    @Getter
    private ModifierDTO modifier;

    @Getter
    private List<String> gameIds;

    @Getter
    private GameConfigDTO config;

    @Getter
    private Long createdAt;

}
