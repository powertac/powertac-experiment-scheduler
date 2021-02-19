package org.powertac.rachma.instance;

public interface InstanceDuplicator {

    Instance createCopy(Instance instance);
    Instance createNamedCopy(String name, Instance instance);

}
