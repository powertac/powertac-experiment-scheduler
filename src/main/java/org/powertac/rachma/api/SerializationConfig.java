package org.powertac.rachma.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.Setter;
import org.powertac.rachma.file.File;
import org.powertac.rachma.file.FileDeserializer;
import org.powertac.rachma.game.*;
import org.powertac.rachma.job.Job;
import org.powertac.rachma.job.JobStatus;
import org.powertac.rachma.job.serialization.JobDeserializer;
import org.powertac.rachma.job.serialization.JobSerializer;
import org.powertac.rachma.job.serialization.JobStatusDeserializer;
import org.powertac.rachma.job.serialization.JobStatusSerializer;
import org.powertac.rachma.powertac.broker.BrokerSerializer;
import org.powertac.rachma.treatment.Modifier;
import org.powertac.rachma.treatment.ModifierDeserializer;
import org.powertac.rachma.treatment.ModifierSerializer;
import org.powertac.rachma.weather.WeatherConfiguration;
import org.powertac.rachma.weather.WeatherConfigurationSerializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SerializationConfig implements ApplicationContextAware {

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
        mapper.findAndRegisterModules().configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        return mapper;
    }

    private Module createSerializationModule() {
        SimpleModule module = new SimpleModule();
        registerSerializers(module);
        registerDeserializers(module);
        registerJobSerialization(module);
        return module;
    }

    private void registerSerializers(SimpleModule module) {
        module.addSerializer(GameRun.class, new GameRunSerializer());
        module.addSerializer(Game.class, new GameSerializer(applicationContext.getBean(GameFileManager.class)));
        module.addSerializer(WeatherConfiguration.class, new WeatherConfigurationSerializer());
        module.addSerializer(Modifier.class, new ModifierSerializer());
    }

    private void registerDeserializers(SimpleModule module) {
        module.addDeserializer(File.class, applicationContext.getBean(FileDeserializer.class));
        module.addDeserializer(Game.class, new GameDeserializer());
        module.addDeserializer(Modifier.class, new ModifierDeserializer());
    }

    @Deprecated
    private void registerJobSerialization(SimpleModule module) {
        module.addSerializer(Job.class, new JobSerializer());
        module.addDeserializer(Job.class, applicationContext.getBean(JobDeserializer.class));
        module.addSerializer(JobStatus.class, new JobStatusSerializer());
        module.addDeserializer(JobStatus.class, new JobStatusDeserializer());
        module.addSerializer(org.powertac.rachma.powertac.broker.Broker.class, new BrokerSerializer());
        module.addDeserializer(org.powertac.rachma.powertac.broker.Broker.class, new org.powertac.rachma.powertac.broker.BrokerDeserializer());
    }

}
