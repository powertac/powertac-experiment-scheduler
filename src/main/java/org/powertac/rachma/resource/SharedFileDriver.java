package org.powertac.rachma.resource;

import java.io.IOException;

// TODO : is this necessary? Probably not...
public interface SharedFileDriver {

    boolean exists(SharedFile file);

    void create(SharedFile file) throws IOException;

}
