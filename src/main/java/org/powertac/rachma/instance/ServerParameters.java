package org.powertac.rachma.instance;

import org.powertac.rachma.configuration.ConfigurationParameter;

import java.util.Map;
import java.util.Set;

public interface ServerParameters {

    Map<String, String> getParameters();

    boolean has(String key);

    void set(String key, String value);

    // TODO : REFACTOR : merge ServerParameters and ConfigurationParameter concepts
    Set<ConfigurationParameter> toConfigurationParameterSet();

}
