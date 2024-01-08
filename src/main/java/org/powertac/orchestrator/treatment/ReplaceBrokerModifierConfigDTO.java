package org.powertac.orchestrator.treatment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
public class ReplaceBrokerModifierConfigDTO extends ModifierConfigDTO {

    @Getter
    private Map<String, String> brokerMapping;

}
