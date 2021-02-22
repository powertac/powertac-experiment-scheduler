package org.powertac.rachma.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.powertac.rachma.powertac.broker.exception.BrokerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class CachedDirectoryBasedBrokerTypeRepository implements BrokerTypeRepository {

    @Value("${directory.local.brokers}")
    private String brokersDir;

    @Value("${broker.policy.descriptorFileName}")
    private String descriptorFileName;

    private final ObjectMapper mapper;

    private Map<String, BrokerType> brokers = new HashMap<>();

    private long cachedAt = Long.MIN_VALUE;

    @Autowired
    public CachedDirectoryBasedBrokerTypeRepository(ObjectMapper objectMapper) {
        mapper = objectMapper;
    }

    @Override
    public BrokerType findByName(String name) throws BrokerNotFoundException {

        updateCache();

        if (!brokers.containsKey(name)) {
            throw new BrokerNotFoundException("could not find broker with name '"+name+"'");
        }

        return brokers.get(name);
    }

    @Override
    public Map<String, BrokerType> findAll() {
        updateCache();
        return brokers;
    }

    @Override
    public boolean has(String name) {
        try {
            findByName(name);
            return true;
        }
        catch (BrokerNotFoundException e) {
            return false;
        }
    }

    private void updateCache() {
        try {
            // TODO : only walk through direct descendants of brokersDir
            Files.walk(Paths.get(brokersDir))
                .filter(Files::isDirectory)
                .map(Path::toFile)
                .filter(f -> f.lastModified() > cachedAt)
                .filter(this::isValidBrokerDir)
                .map(this::createBrokerFromDir)
                .filter(Objects::nonNull)
                .forEach(broker -> brokers.put(broker.getName(), broker));

            cachedAt = new Date().getTime();

            // TODO : clear brokers that no longer exist on filesystem
        }
        catch (IOException e) {
            // TODO : do something meaningful
        }
    }

    private boolean isValidBrokerDir(File dir) {
        File descriptor = new File(dir.getAbsolutePath() + File.separator + descriptorFileName);
        return descriptor.exists();
    }

    private BrokerType createBrokerFromDir(File dir) {
        try {
            BrokerType type = mapper.readValue(new File(dir.getAbsolutePath() + File.separator + descriptorFileName), BrokerType.class);
            type.setPath(dir.getAbsolutePath());
            return type.isEnabled() ? type : null;
        }
        catch (IOException e) {
            // TODO : log exception
            return null;
        }
    }
}
