package org.powertac.rachma.game;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.powertac.rachma.broker.BrokerSet;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = GameConfigDeserializer.class)
public class GameConfig {

    @Id
    @Getter
    @Setter
    @Column(length = 36)
    private String id;

    @Getter
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private BrokerSet brokers;

    @Getter
    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "game_config_parameters", joinColumns = {@JoinColumn(name = "game_config_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "parameter", length = 128)
    @Column(name = "value")
    private Map<String, String> parameters = new HashMap<>();

    // TODO : file mappings

}
