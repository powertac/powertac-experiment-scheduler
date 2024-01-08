package org.powertac.orchestrator.analysis;

import java.util.Optional;
import java.util.Set;

public interface AnalyzerProvider {

    Set<Analyzer> getAvailableAnalyzers();
    boolean has(String name);
    Optional<Analyzer> get(String name);

}
