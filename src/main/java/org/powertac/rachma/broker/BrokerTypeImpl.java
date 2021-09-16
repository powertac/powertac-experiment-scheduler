package org.powertac.rachma.broker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Deprecated
public class BrokerTypeImpl implements BrokerType {

    @Getter
    private final String name;

    @Getter
    private final String image;

    @Getter
    private final boolean enabled;

    @Getter
    @Setter
    private String path;

    public BrokerTypeImpl(String name, String image) {
        this.name = name;
        this.image = image;
        this.enabled = true;
    }

    public BrokerTypeImpl(String name, String image, boolean isEnabled) {
        this.name = name;
        this.image = image;
        this.enabled = isEnabled;
    }

}
