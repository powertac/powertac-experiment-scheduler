package org.powertac.rachma.api.stomp;

public interface EntityPublisher<T> {

    void publish(T entity);

}
