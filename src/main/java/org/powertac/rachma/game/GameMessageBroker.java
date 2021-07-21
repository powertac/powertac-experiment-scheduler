package org.powertac.rachma.game;

import org.powertac.rachma.api.stomp.AbstractStompMessageBroker;
import org.powertac.rachma.api.stomp.StompMessageBroker;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class GameMessageBroker extends AbstractStompMessageBroker<Game> implements StompMessageBroker<Game> {

    public GameMessageBroker(SimpMessagingTemplate template) {
        super(template, "/games");
    }

}
