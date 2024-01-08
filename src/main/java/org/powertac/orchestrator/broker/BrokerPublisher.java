package org.powertac.orchestrator.broker;

import org.powertac.orchestrator.api.stomp.AbstractEntityPublisher;
import org.powertac.orchestrator.api.stomp.EntityPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class BrokerPublisher extends AbstractEntityPublisher<Broker> implements EntityPublisher<Broker> {

    public BrokerPublisher(SimpMessagingTemplate template) {
        super(template, "/brokers");
    }

}
