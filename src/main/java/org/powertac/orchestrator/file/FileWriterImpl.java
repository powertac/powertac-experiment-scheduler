package org.powertac.orchestrator.file;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class FileWriterImpl implements FileWriter {

    public final static Charset defaultCharset = StandardCharsets.UTF_8;

    @Override
    public void createFileIfNotExists(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
    }

    @Override
    public void createDirectoryIfNotExists(Path dirPath) throws IOException {
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
    }

    @Override
    public void write(Path filePath, String contents) throws IOException {
        Files.write(filePath, contents.getBytes(defaultCharset));
    }

}
