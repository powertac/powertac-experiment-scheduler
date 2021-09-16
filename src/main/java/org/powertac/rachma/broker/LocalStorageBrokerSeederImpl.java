package org.powertac.rachma.broker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.docker.DockerImageRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LocalStorageBrokerSeederImpl implements BrokerSeeder {

    private final BrokerRepository brokers;
    private final BrokerTypeRepository types;
    private final DockerImageRepository images;
    private final Logger logger;

    public LocalStorageBrokerSeederImpl(BrokerRepository brokers, BrokerTypeRepository types, DockerImageRepository images) {
        this.brokers = brokers;
        this.types = types;
        this.images = images;
        this.logger = LogManager.getLogger(BrokerSeeder.class);
    }

    @Override
    public void seedBrokers() {
        for (BrokerType type : types.findAll().values()) {
            try {
                Broker broker = brokers.findByNameAndVersion(type.getName(), "latest");
                if (null == broker) {
                    String id = UUID.randomUUID().toString();
                    broker = new Broker(id, type.getName(), "latest", type.getImage(), true);
                } else if (null == broker.getImageTag()) {
                    broker.setImageTag(type.getImage());
                    broker.setEnabled(images.exists(type.getImage()));
                }
                brokers.save(broker);
            } catch (BrokerConflictException e) {
                logger.error(String.format("could not seed broker with name '%s' due to conflict with existing broker", type.getName()), e);
            }
        }
    }

}
