package org.powertac.orchestrator.file;

import java.io.IOException;
import java.nio.file.Path;

public interface FileWriter {

    void createFileIfNotExists(Path filePath) throws IOException;
    void createDirectoryIfNotExists(Path dirPath) throws IOException;
    void write(Path filePath, String contents) throws IOException;

}
