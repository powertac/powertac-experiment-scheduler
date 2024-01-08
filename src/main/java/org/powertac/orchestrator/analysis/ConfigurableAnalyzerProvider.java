package org.powertac.orchestrator.analysis;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ConfigurableAnalyzerProvider implements AnalyzerProvider {

    private final Set<Analyzer> availableAnalyzers = new HashSet<>();

    @Override
    public Set<Analyzer> getAvailableAnalyzers() {
        return new HashSet<>(availableAnalyzers);
    }

    @Override
    public boolean has(String name) {
        return get(name).isPresent();
    }

    @Override
    public Optional<Analyzer> get(String name) {
        for (Analyzer analyzer : availableAnalyzers) {
            if (analyzer.getName().equals(name)) {
                return Optional.of(analyzer);
            }
        }
        return Optional.empty();
    }

    public void addAnalyzer(Analyzer analyzer) {
        availableAnalyzers.add(analyzer);
    }

}
