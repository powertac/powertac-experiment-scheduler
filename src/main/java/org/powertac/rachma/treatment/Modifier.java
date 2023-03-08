package org.powertac.rachma.treatment;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@JsonDeserialize(using = ModifierDeserializer.class)
public abstract class Modifier {

    @Id
    @Getter
    @Setter
    @Column(length = 36)
    private String id;

    @Getter
    @Setter
    private String name;

    abstract public ModifierType getType();

}
