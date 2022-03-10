package org.powertac.rachma.file;

import java.io.IOException;
import java.nio.file.Path;

public interface FileWriter {

    void createFileIfNotExists(Path filePath) throws IOException;
    void createDirectoryIfNotExists(Path dirPath) throws IOException;

}
