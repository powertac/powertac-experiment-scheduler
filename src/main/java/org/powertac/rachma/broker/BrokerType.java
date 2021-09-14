package org.powertac.rachma.broker;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.powertac.rachma.powertac.broker.BrokerTypeSerializer;

@JsonSerialize(using = BrokerTypeSerializer.class)
@JsonDeserialize(using = BrokerTypeDeserializer.class)
public interface BrokerType {

    String getName();
    String getImage();
    boolean isEnabled();

    @JsonIgnore
    String getPath();
    void setPath(String path);

}
