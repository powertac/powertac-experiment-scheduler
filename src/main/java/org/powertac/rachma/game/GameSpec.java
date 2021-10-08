package org.powertac.rachma.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.powertac.rachma.broker.Broker;

import java.util.Map;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
public class GameSpec {

    @Getter
    private String name;

    @Getter
    private String baseGameId;

    @Getter
    private Set<Broker> brokers;

    @Getter
    private Map<String, String> serverParameters;

}
