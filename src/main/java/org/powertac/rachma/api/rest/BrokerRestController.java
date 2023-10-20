package org.powertac.rachma.api.rest;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.broker.*;
import org.powertac.rachma.docker.DockerImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("brokers")
public class BrokerRestController {

    private final BrokerRepository brokers;
    private final DockerImageRepository images;
    private final BrokerTypeRepository brokerTypeRepository;
    private final Logger logger;

    @Autowired
    public BrokerRestController(BrokerRepository brokers, DockerImageRepository images, BrokerTypeRepository brokerTypeRepository) {
        this.brokers = brokers;
        this.images = images;
        this.brokerTypeRepository = brokerTypeRepository;
        this.logger = LogManager.getLogger(BrokerRestController.class);
    }

    @GetMapping("/types")
    @Deprecated
    public Object types() {
        return new Object() {
            @Getter boolean success = true;
            @Getter List<BrokerType> payload = new ArrayList<>(brokerTypeRepository.findAll().values());
        };
    }

    @GetMapping("/")
    public ResponseEntity<Collection<Broker>> getBrokers() {
        return ResponseEntity.ok(brokers.findAll());
    }

    @PostMapping("/")
    public ResponseEntity<?> createBroker(@RequestBody Broker broker) {
        try {
            broker.setEnabled(images.exists(broker.getImageTag()));
            brokers.save(broker);
            return ResponseEntity.ok().build();
        } catch (BrokerConflictException|BrokerValidationException e) {
            logger.error("error creating broker", e);
            return ResponseEntity.badRequest().build();
        }
    }

}
