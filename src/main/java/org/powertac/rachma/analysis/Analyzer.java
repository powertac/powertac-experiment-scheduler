package org.powertac.rachma.analysis;

import lombok.Builder;
import lombok.Getter;
import org.powertac.rachma.artifact.ArtifactProducer;

import java.util.Set;

@Builder
@Getter
public class Analyzer implements ArtifactProducer {

    private String name;
    private Set<ArtifactProducer> requirements;
    private AnalyzerScope scope;

}
