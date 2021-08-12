package org.powertac.rachma.application;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.Setter;
import org.powertac.rachma.file.File;
import org.powertac.rachma.file.FileDeserializer;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameDeserializer;
import org.powertac.rachma.game.GameSerializer;
import org.powertac.rachma.instance.Instance;
import org.powertac.rachma.instance.InstanceDeserializer;
import org.powertac.rachma.instance.ServerParameters;
import org.powertac.rachma.instance.ServerParametersSerializer;
import org.powertac.rachma.job.Job;
import org.powertac.rachma.job.JobStatus;
import org.powertac.rachma.job.serialization.JobDeserializer;
import org.powertac.rachma.job.serialization.JobSerializer;
import org.powertac.rachma.job.serialization.JobStatusDeserializer;
import org.powertac.rachma.job.serialization.JobStatusSerializer;
import org.powertac.rachma.powertac.broker.Broker;
import org.powertac.rachma.powertac.broker.serialization.BrokerDeserializer;
import org.powertac.rachma.powertac.broker.serialization.BrokerSerializer;
import org.powertac.rachma.treatment.Treatment;
import org.powertac.rachma.treatment.TreatmentDeserializer;
import org.powertac.rachma.treatment.TreatmentSerializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig implements ApplicationContextAware {

    @Setter
    private ApplicationContext applicationContext;

    @Bean
    public CommonAnnotationBeanPostProcessor beanPostProcessor() {
        return new CommonAnnotationBeanPostProcessor();
    }

    @Bean
    public ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(createSerializationModule());
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper;
    }

    private Module createSerializationModule() {
        SimpleModule module = new SimpleModule();
        registerJobSerialization(module);
        registerBrokerSerialization(module);
        return module;
    }

    private void registerJobSerialization(SimpleModule module) {
        module.addSerializer(Job.class, new JobSerializer());
        module.addDeserializer(Job.class, applicationContext.getBean(JobDeserializer.class));
        module.addSerializer(JobStatus.class, new JobStatusSerializer());
        module.addDeserializer(JobStatus.class, new JobStatusDeserializer());
        module.addDeserializer(Instance.class, new InstanceDeserializer());
        module.addDeserializer(Treatment.class, new TreatmentDeserializer());
        module.addSerializer(Treatment.class, new TreatmentSerializer());
        module.addSerializer(ServerParameters.class, new ServerParametersSerializer());
        module.addDeserializer(org.powertac.rachma.broker.Broker.class, new org.powertac.rachma.broker.BrokerDeserializer());
        module.addDeserializer(Game.class, new GameDeserializer());
        //module.addSerializer(Game.class, new GameSerializer());
        module.addDeserializer(File.class, applicationContext.getBean(FileDeserializer.class));
    }

    private void registerBrokerSerialization(SimpleModule module) {
        module.addSerializer(Broker.class, new BrokerSerializer());
        module.addDeserializer(Broker.class, new BrokerDeserializer());
    }

}
