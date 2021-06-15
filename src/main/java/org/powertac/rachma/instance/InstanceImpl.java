package org.powertac.rachma.instance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.job.JobStatus;
import org.springframework.data.annotation.Id;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
public class InstanceImpl implements Instance {

    @Id
    @Getter
    @Setter
    private String id;

    @Getter
    private String name;

    @Getter
    private Set<Broker> brokers;

    @Getter
    private ServerParameters serverParameters;

    @Getter
    private String bootstrapFile;

    @Getter
    private String seedFile;

    @Getter
    @Setter
    private JobStatus status;

    public InstanceImpl(Set<Broker> brokers, ServerParameters serverParameters) {
        this.brokers = brokers;
        this.serverParameters = serverParameters;
    }

    public InstanceImpl(String name, Set<Broker> brokers, ServerParameters serverParameters) {
        this.name = name;
        this.brokers = brokers;
        this.serverParameters = serverParameters;
    }

    public InstanceImpl(String name, Set<Broker> brokers, ServerParameters serverParameters, String bootstrapFile, String seedFile) {
        this.name = name;
        this.brokers = brokers;
        this.serverParameters = serverParameters;
        this.bootstrapFile = bootstrapFile;
        this.seedFile = seedFile;
    }

    public InstanceImpl(String id, String name, Set<Broker> brokers, ServerParameters serverParameters, String bootstrapFile, String seedFile) {
        this.id = id;
        this.name = name;
        this.brokers = brokers;
        this.serverParameters = serverParameters;
        this.bootstrapFile = bootstrapFile;
        this.seedFile = seedFile;
    }

}
