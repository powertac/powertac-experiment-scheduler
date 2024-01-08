package org.powertac.orchestrator.exec;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PersistentTaskDTO<C> {

    private String type;
    private String id;
    private String creatorId;
    private Long createdAt;
    private Long start;
    private Long end;
    private Integer priority;
    private boolean failed;
    private C config;

}
