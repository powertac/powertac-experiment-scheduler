package org.powertac.rachma.instance;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ParameterImpl implements Parameter {

    @Getter
    private String key;

    @Getter
    private String value;

}
