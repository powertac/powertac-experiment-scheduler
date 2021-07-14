package org.powertac.rachma.broker;

import java.util.Map;

public interface Broker {

    String getName();

    String getVersion();

    @Deprecated
    Map<String, String> getConfig();

}
