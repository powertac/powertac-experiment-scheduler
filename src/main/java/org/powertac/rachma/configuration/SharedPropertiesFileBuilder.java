package org.powertac.rachma.configuration;

import org.powertac.rachma.resource.SharedFile;
import org.powertac.rachma.resource.SharedFileBuilder;
import org.powertac.rachma.resource.WorkDirectory;
import org.powertac.rachma.util.BrokerCompatiblePropertiesWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

public class SharedPropertiesFileBuilder {

    private WorkDirectory workDirectory;
    private String containerDirectory;
    private String file;
    private Properties properties = new Properties();


    public static SharedPropertiesFileBuilder newFile() {
        return new SharedPropertiesFileBuilder();
    }

    public SharedPropertiesFileBuilder workDirectory(WorkDirectory workDirectory) {
        this.workDirectory = workDirectory;
        return this;
    }

    public SharedPropertiesFileBuilder containerDirectory(String containerDirectory) {
        this.containerDirectory = containerDirectory;
        return this;
    }

    public SharedPropertiesFileBuilder file(String file) {
        this.file = file;
        return this;
    }

    public SharedPropertiesFileBuilder properties(Properties properties) {
        this.properties = properties;
        return this;
    }

    public SharedPropertiesFileBuilder properties(Map<String, String> properties) {
        this.properties = new Properties();
        this.properties.putAll(properties);
        return this;
    }

    public SharedPropertiesFileBuilder addProperties(Properties properties) {
        this.properties.putAll(properties);
        return this;
    }

    public SharedPropertiesFileBuilder addProperties(Map<String, String> properties) {
        this.properties.putAll(properties);
        return this;
    }

    public SharedPropertiesFileBuilder property(String key, String value) {
        this.properties.put(key, value);
        return this;
    }

    public SharedFile writeAndBuild() throws IOException {

        SharedFile propertiesFile = SharedFileBuilder.create()
            .localDirectory(workDirectory.getLocalDirectory())
            .hostDirectory(workDirectory.getHostDirectory())
            .containerDirectory(containerDirectory)
            .file(file)
            .build();


        writeProperties(propertiesFile.getLocalPath(), properties);

        return propertiesFile;
    }

    private static void writeProperties(String targetFile, Properties properties) throws IOException {
        createParentDirectory(targetFile);
        BrokerCompatiblePropertiesWriter.write(targetFile, properties);
    }

    private static void createParentDirectory(String targetFile) throws IOException {
        Path parentDirectory = Paths.get(targetFile).getParent();
        if (!Files.exists(parentDirectory)) {
            Files.createDirectories(parentDirectory);
        }
    }

}
