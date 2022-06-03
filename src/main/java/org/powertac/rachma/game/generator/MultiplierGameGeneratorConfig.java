package org.powertac.rachma.game.generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.powertac.rachma.game.GameConfig;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class MultiplierGameGeneratorConfig extends GameGeneratorConfig {

    public final static String TYPE_ID = "game-multiplier";

    @Setter
    @Getter
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private GameConfig gameConfig;

    @Setter
    @Getter
    private Integer multiplier;

    public String getType() {
        return TYPE_ID;
    }

}
