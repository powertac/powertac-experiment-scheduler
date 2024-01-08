package org.powertac.orchestrator.api.stomp;

public interface EntityPublisher<T> {

    void publish(T entity);

}
