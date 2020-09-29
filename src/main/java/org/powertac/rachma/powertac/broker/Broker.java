package org.powertac.rachma.powertac.broker;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.powertac.rachma.broker.BrokerType;
import org.powertac.rachma.powertac.broker.serialization.BrokerSerializer;

// TODO : merge broker models
@JsonSerialize(using = BrokerSerializer.class)
public interface Broker {

    String getId();
    String getName();
    BrokerType getType();

}
