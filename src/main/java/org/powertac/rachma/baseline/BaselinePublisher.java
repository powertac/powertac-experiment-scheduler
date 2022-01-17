package org.powertac.rachma.baseline;

import org.powertac.rachma.api.stomp.AbstractEntityPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class BaselinePublisher extends AbstractEntityPublisher<Baseline> {

    public BaselinePublisher(SimpMessagingTemplate template) {
        super(template, "/baselines");
    }

}
