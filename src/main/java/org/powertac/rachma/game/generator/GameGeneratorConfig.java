package org.powertac.rachma.game.generator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@JsonDeserialize(using = GameGeneratorConfigDeserializer.class)
public abstract class GameGeneratorConfig {

    @Id
    @Getter
    @Setter
    @Column(length = 36)
    private String id;

    abstract public String getType();

}
