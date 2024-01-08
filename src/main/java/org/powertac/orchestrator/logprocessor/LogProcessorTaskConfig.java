package org.powertac.orchestrator.logprocessor;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Builder
@Getter
public class LogProcessorTaskConfig {

    private String gameId;
    private Set<String> processorNames;

}
