package org.powertac.rachma.logprocessor;

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
    public boolean hasProcessor(String processorName) {
        for (LogProcessor processor : availableProcessors) {
            if (processor.getName().equals(processorName)) {
                return true;
            }
        }
        return false;
    }
}
