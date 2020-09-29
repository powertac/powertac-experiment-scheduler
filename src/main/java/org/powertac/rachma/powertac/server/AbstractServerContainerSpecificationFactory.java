package org.powertac.rachma.powertac.server;

import org.powertac.rachma.configuration.SharedPropertiesFileBuilder;
import org.powertac.rachma.resource.SharedFile;
import org.powertac.rachma.resource.SharedFileBuilder;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public abstract class AbstractServerContainerSpecificationFactory<T extends ServerTask> {

    @Value("${server.defaultImage}")
    protected String defaultServerImageTag;

    @Value("${container.directory.base}")
    private String containerBaseDir;

    @Value("${server.bootstrap.defaultPropertiesFile}")
    private String defaultPropertiesFile;


    protected SharedFile createSharedBootstrapFile(T task) throws IOException {
        return SharedFileBuilder.create()
            .localDirectory(task.getJob().getWorkDirectory().getLocalDirectory())
            .hostDirectory(task.getJob().getWorkDirectory().getHostDirectory())
            .containerDirectory(containerBaseDir)
            .file("bootstrap.xml")
            .build();
    }

    protected SharedFile createSharedPropertiesFile(T task, String propertiesFileName) throws IOException {
        return SharedPropertiesFileBuilder.newFile()
            .workDirectory(task.getWorkDirectory())
            .containerDirectory(containerBaseDir)
            .file(propertiesFileName)
            .properties(getDefaultProperties())
            .addProperties(task.getParameters())
            .writeAndBuild();
    }

    private Properties getDefaultProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(Files.newInputStream(Paths.get(defaultPropertiesFile)));
        return properties;
    }

}
