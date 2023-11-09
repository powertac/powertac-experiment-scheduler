package org.powertac.rachma.logprocessor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LogProcessorArtifactDTO {

    private String processorName;
    private String filePath;

}
