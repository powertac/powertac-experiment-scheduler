package org.powertac.rachma.treatment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
public class ParameterSetModifierConfigDTO extends ModifierConfigDTO {

    @Getter
    Map<String, String> parameters;

}
