package org.powertac.rachma.treatment;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@NoArgsConstructor
@JsonDeserialize(using = ModifierDeserializer.class)
public class ParameterSetModifier extends Modifier {

    @Setter
    @Getter
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "modified_parameters", joinColumns = {@JoinColumn(name = "modifier_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "parameter", length = 128)
    @Column(name = "value")
    private Map<String, String> parameters = new HashMap<>();

    @JsonGetter
    public String getType() {
        return "parameter-set";
    }

    public ParameterSetModifier(String id, String name, Map<String, String> parameters) {
        setId(id);
        setName(name);
        setParameters(parameters);
    }

}
