package org.powertac.rachma.api.stomp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;

// TODO : unnecessary abstraction
abstract public class AbstractEntityPublisher<T> implements EntityPublisher<T> {

    private final SimpMessagingTemplate template;
    private final String destination;

    private final Logger logger;

    protected AbstractEntityPublisher(SimpMessagingTemplate template, String destination) {
        this.template = template;
        this.destination = destination;
        logger = LogManager.getLogger(AbstractEntityPublisher.class);
    }

    @Override
    public void publish(T entity) {
        try {
            template.convertAndSend(destination, entity);
        } catch (MessagingException e) {
            logger.error(e);
        }
    }

}
