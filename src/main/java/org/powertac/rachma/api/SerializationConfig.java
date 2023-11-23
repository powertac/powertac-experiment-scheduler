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
import org.powertac.rachma.paths.HostPathSerializer;
import org.powertac.rachma.paths.PathProvider;
import org.powertac.rachma.paths.PathTranslator;
import org.powertac.rachma.treatment.Modifier;
import org.powertac.rachma.treatment.ModifierDeserializer;
import org.powertac.rachma.weather.WeatherConfiguration;
import org.powertac.rachma.weather.WeatherConfigurationSerializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

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
        return module;
    }

    private void registerSerializers(SimpleModule module) {
        module.addSerializer(GameRun.class, new GameRunSerializer());
        module.addSerializer(Game.class, new GameSerializer(applicationContext.getBean(PathProvider.class)));
        module.addSerializer(WeatherConfiguration.class, new WeatherConfigurationSerializer());
        module.addSerializer(Path.class, new HostPathSerializer(applicationContext.getBean(PathTranslator.class)));
    }

    private void registerDeserializers(SimpleModule module) {
        module.addDeserializer(File.class, applicationContext.getBean(FileDeserializer.class));
        module.addDeserializer(Game.class, new GameDeserializer());
        module.addDeserializer(Modifier.class, new ModifierDeserializer());
    }

}
