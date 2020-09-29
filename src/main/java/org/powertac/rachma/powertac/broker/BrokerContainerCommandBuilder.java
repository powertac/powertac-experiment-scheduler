package org.powertac.rachma.powertac.broker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BrokerContainerCommandBuilder {

    private List<String> options = new ArrayList<>();

    public static BrokerContainerCommandBuilder builder() {
        return new BrokerContainerCommandBuilder();
    }

    public BrokerContainerCommandBuilder withOption(String name, String value) {
        options.add(name);
        options.addAll(Arrays.stream(value.split("\\s"))
            .map(s -> s.replace(" ", ""))
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList()));
        return this;
    }

    public BrokerContainerCommandBuilder clearOptions() {
        options = new ArrayList<>();
        return this;
    }

    public BrokerDockerContainerCommand build() {
        return new BrokerDockerContainerCommand(options);
    }

}
