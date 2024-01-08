package org.powertac.orchestrator.util;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * This is a workaround to pass URIs to brokers based on the sample broker package since it does not accept quoted
 * colons in property files
 *
 * TODO : make sample broker properties reading compliant
 */
public class BrokerCompatiblePropertiesWriter {

    public static void write(String file, Properties properties) throws IOException {
        properties.store(new FileWriter(file), null);
        replaceQuotedColons(file);
    }

    private static void replaceQuotedColons(String file) throws IOException {
        Path path = Paths.get(file);
        Charset charset = StandardCharsets.ISO_8859_1;
        String content = Files.readString(path, charset);
        content = content.replaceAll("\\\\:", ":");
        Files.write(path, content.getBytes(charset));
    }

}
