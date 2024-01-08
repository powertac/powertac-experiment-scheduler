package org.powertac.orchestrator.treatment;

import lombok.Getter;

public class ModifierDTO extends NewModifierDTO {

    @Getter
    private String id;

    public ModifierDTO(String id, ModifierType type, ModifierConfigDTO config) {
        this.id = id;
        this.type = type;
        this.config = config;
    }

}
