package org.powertac.orchestrator.logprocessor;

import java.util.HashSet;
import java.util.Set;

public class ConfigurableLogProcessorProvider implements LogProcessorProvider {

    private final Set<LogProcessor> availableProcessors = new HashSet<>();

    public void addProcessor(LogProcessor processor) {
        availableProcessors.add(processor);
    }

    @Override
    public Set<LogProcessor> getAvailableProcessors() {
        return new HashSet<>(availableProcessors);
    }

    @Override
    public boolean has(String processorName) {
        return get(processorName) != null;
    }

    @Override
    public LogProcessor get(String processorName) {
        for (LogProcessor processor : availableProcessors) {
            if (processor.getName().equals(processorName)) {
                return processor;
            }
        }
        return null;
    }

}
