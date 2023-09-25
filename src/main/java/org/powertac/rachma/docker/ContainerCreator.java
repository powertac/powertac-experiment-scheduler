package org.powertac.rachma.docker;

public interface ContainerCreator<E> {

    DockerContainer createFor(E entity);

}
