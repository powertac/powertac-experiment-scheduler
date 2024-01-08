package org.powertac.orchestrator.logprocessor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.powertac.orchestrator.artifact.ArtifactProducer;

@AllArgsConstructor
public class LogProcessor implements ArtifactProducer {

    @Getter
    private String name;

    @Getter
    private String clazz;

    @Getter
    private String fileNamePattern;

}
