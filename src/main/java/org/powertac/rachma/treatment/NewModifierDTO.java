package org.powertac.rachma.treatment;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = NewModifierDTODeserializer.class)
public class NewModifierDTO {

    @Setter
    @Getter
    protected ModifierType type;

    @Setter
    @Getter
    protected ModifierConfigDTO config;

}
