package org.powertac.orchestrator.analysis;

import lombok.Builder;
import lombok.Getter;
import org.powertac.orchestrator.artifact.ArtifactProducer;

import java.util.Set;

@Builder
@Getter
public class Analyzer implements ArtifactProducer {

    private String name;
    private Set<ArtifactProducer> requirements;
    private AnalyzerScope scope;

}
