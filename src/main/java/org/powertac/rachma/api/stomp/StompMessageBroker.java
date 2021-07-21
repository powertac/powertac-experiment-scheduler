package org.powertac.rachma.api.stomp;

public interface StompMessageBroker<T> {

    void publish(T entity);

}
