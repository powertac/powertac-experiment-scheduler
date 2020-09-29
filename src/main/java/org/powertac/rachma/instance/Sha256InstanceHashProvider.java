package org.powertac.rachma.instance;

import com.google.common.hash.Hashing;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.hash.HashProvider;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Sha256InstanceHashProvider implements HashProvider<Instance> {

    private final HashProvider<Broker> brokerHashProvider;

    public Sha256InstanceHashProvider(HashProvider<Broker> brokerHashProvider) {
        this.brokerHashProvider = brokerHashProvider;
    }

    @Override
    public String getHash(Instance instance) {
        List<Broker> orderedBrokers = orderBrokers(instance.getBrokers());
        Stream<String> brokerHashes = orderedBrokers.stream().map(brokerHashProvider::getHash);
        // TODO : how to hash server parameters?
        Stream<String> serverParametersHash = Stream.of(instance.getServerParameters().toString());
        String concatenatedHashes = Stream.concat(brokerHashes, serverParametersHash)
            .reduce("", (a,b) -> a + b);
        return Hashing.sha256().hashString(concatenatedHashes, StandardCharsets.UTF_8).toString();
    }

    private List<Broker> orderBrokers(Set<Broker> brokers) {
        // TODO : add order function
        return new ArrayList<>(brokers);
    }

}
