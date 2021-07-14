package org.powertac.rachma.broker;

import org.powertac.rachma.powertac.broker.exception.BrokerNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class BrokerImageResolverImpl implements BrokerImageResolver {

    private final BrokerTypeRepository types;

    public BrokerImageResolverImpl(BrokerTypeRepository types) {
        this.types = types;
    }

    @Override
    public String getImageTag(Broker broker) {
        try {
            return types.findByName(broker.getName()).getImage();
        } catch (BrokerNotFoundException e) {
            return null;
        }
    }

}
