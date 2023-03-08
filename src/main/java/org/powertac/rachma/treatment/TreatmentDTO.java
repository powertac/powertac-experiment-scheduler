package org.powertac.rachma.treatment;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
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
    private Instant createdAt;
}
