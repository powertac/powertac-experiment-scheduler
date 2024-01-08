package org.powertac.orchestrator.baseline;

import org.powertac.orchestrator.api.stomp.AbstractEntityPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class BaselinePublisher extends AbstractEntityPublisher<Baseline> {

    public BaselinePublisher(SimpMessagingTemplate template) {
        super(template, "/baselines");
    }

}
