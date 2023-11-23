package org.powertac.rachma.game.generator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

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
