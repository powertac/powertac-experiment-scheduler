package org.powertac.orchestrator.docker;

public interface ContainerCreator<E> {

    DockerContainer createFor(E entity);

}
