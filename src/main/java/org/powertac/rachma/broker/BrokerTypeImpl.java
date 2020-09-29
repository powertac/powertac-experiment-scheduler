package org.powertac.rachma.broker;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class BrokerTypeImpl implements BrokerType {

    @Getter
    private final String name;

    @Getter
    private final String image;

    @Getter
    private final boolean enabled;

    public BrokerTypeImpl(String name, String image) {
        this.name = name;
        this.image = image;
        this.enabled = true;
    }

}
