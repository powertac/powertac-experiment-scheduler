package org.powertac.rachma.game;

import org.powertac.rachma.api.stomp.AbstractEntityPublisher;
import org.powertac.rachma.api.stomp.EntityPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class GamePublisher extends AbstractEntityPublisher<Game> implements EntityPublisher<Game> {

    public GamePublisher(SimpMessagingTemplate template) {
        super(template, "/games");
    }

}
