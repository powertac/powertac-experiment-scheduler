package org.powertac.rachma.configuration;

import org.powertac.rachma.configuration.exception.ParameterValidationException;

public interface ConfigurationParameterValidator {

    void validate(String key, String value) throws ParameterValidationException;

}
