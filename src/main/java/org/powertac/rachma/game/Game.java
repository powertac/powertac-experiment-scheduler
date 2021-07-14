package org.powertac.rachma.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.file.File;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
public class Game {

    @Getter
    @Setter
    private String id;

    @Getter
    private final String name;

    @Getter
    private final Set<Broker> brokers;

    @Getter
    private final Map<String, String> serverParameters;

    @Getter
    private final File bootstrap;

    @Getter
    private final File seed;

    @Getter
    private final Instant createdAt;

}
