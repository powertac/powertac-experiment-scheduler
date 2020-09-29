package org.powertac.rachma.treatment;

import org.powertac.rachma.instance.Instance;

public interface Treatment {

    Instance mutate(Instance instanceCopy);

}
