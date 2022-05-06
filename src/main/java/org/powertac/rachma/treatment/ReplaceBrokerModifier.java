package org.powertac.rachma.treatment;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.powertac.rachma.broker.Broker;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
@NoArgsConstructor
@JsonDeserialize(using = ModifierDeserializer.class)
public class ReplaceBrokerModifier extends Modifier {

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private Broker original;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private Broker replacement;

    @JsonGetter
    public final String getType() {
        return "replace-broker";
    }

    @JsonGetter
    public String getOriginalId() {
        return original.getId();
    }

    @JsonGetter
    public String getReplacementId() {
        return replacement.getId();
    }

    public ReplaceBrokerModifier(String id, String name, Broker original, Broker replacement) {
        setId(id);
        setName(name);
        setOriginal(original);
        setReplacement(replacement);
    }

}
