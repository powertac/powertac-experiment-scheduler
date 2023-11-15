package org.powertac.rachma.logprocessor;

import java.util.Set;

public interface LogProcessorProvider {

    Set<LogProcessor> getAvailableProcessors();
    boolean has(String processorName);
    LogProcessor get(String processorName);

}
