package org.powertac.orchestrator.file;

import java.io.IOException;
import java.nio.file.Path;

public interface FileTreeBuilder {

    FileNode build(Path path) throws IOException;
    FileNode build(Path path, int depth) throws IOException;

}
