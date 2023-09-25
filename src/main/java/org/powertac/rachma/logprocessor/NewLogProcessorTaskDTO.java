package org.powertac.rachma.logprocessor;

import lombok.Getter;

import java.util.Set;

public class NewLogProcessorTaskDTO {

    @Getter
    private Set<String> gameIds;

    @Getter
    private Set<String> processorIds;

}
