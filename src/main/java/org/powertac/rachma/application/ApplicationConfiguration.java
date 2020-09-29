package org.powertac.rachma.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.broker.BrokerDeserializer;
import org.powertac.rachma.experiment.*;
import org.powertac.rachma.hash.GenericHashProvider;
import org.powertac.rachma.hash.HashProvider;
import org.powertac.rachma.instance.*;
import org.powertac.rachma.treatment.Treatment;
import org.powertac.rachma.treatment.TreatmentDeserializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;

@Configuration
public class ApplicationConfiguration implements ApplicationContextAware {

    private static final int defaultMongoPort = 27017;
    private static final String database = "powertac";

    @Value("${persistence.mongodb.host}")
    private String mongoHost;

    @Value("${persistence.mongodb.port}")
    private Integer mongoPort;

    @Value("${spring.data.mongodb.username}")
    private String username;

    @Value("${spring.data.mongodb.password}")
    private String password;

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    @Deprecated
    public ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Instance.class, new InstanceDeserializer());
        module.addDeserializer(Treatment.class, new TreatmentDeserializer());
        module.addDeserializer(Broker.class, new BrokerDeserializer());
        mapper.registerModule(module);
        return mapper;
    }

    @SuppressWarnings("unchecked")
    @Bean
    public ExperimentFactory experimentFactory() {
        ExperimentInstanceFactory instanceFactory = context.getBean(ExperimentInstanceFactory.class);
        return new SimpleExperimentFactory(sha256ExperimentHashProvider(), instanceFactory);
    }

    @SuppressWarnings("unchecked")
    public HashProvider<Experiment> sha256ExperimentHashProvider() {
        return new Sha256ExperimentHashProvider(
            sha256InstanceHashProvider(),
            new GenericHashProvider());
    }

    @SuppressWarnings("unchecked")
    public HashProvider<Instance> sha256InstanceHashProvider() {
        return new Sha256InstanceHashProvider(new GenericHashProvider());
    }

    @Deprecated
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), database);
    }

    @Deprecated
    public MongoDbFactory mongoDbFactory() {
        return new SimpleMongoClientDbFactory(getMongoConnectionString());
    }

    @Deprecated
    public MongoClient mongoClient() {
        return MongoClients.create(getMongoSettings());
    }

    @Deprecated
    private MongoClientSettings getMongoSettings() {
        return MongoClientSettings.builder()
            .credential(mongoCredential())
            .retryWrites(true)
            .build();
    }

    @Deprecated
    private ConnectionString getMongoConnectionString() {
        return new ConnectionString(String.format(
            "mongodb://%s:%s/%s",
            mongoHost,
            mongoPort(),
            database));
    }

    @Deprecated
    private Integer mongoPort() {
        if (null != mongoPort) {
            return mongoPort;
        }
        return defaultMongoPort;
    }

    @Deprecated
    private MongoCredential mongoCredential() {
        return MongoCredential.createScramSha256Credential(
            username,
            database,
            password.toCharArray());
    }

}
