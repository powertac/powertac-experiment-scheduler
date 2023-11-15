package org.powertac.rachma.logprocessor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.powertac.rachma.artifact.ArtifactProducer;

@AllArgsConstructor
public class LogProcessor implements ArtifactProducer {

    @Getter
    private String name;

    @Getter
    private String clazz;

    @Getter
    private String fileNamePattern;

}
