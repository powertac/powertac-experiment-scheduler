package org.powertac.orchestrator.file.action;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DeleteFileAction implements FileAction {

    private final Path path;

    public DeleteFileAction(Path path) {
        this.path = path;
    }

    @Override
    public void exec() throws IOException {
        if (!Files.isWritable(path) || !Files.isExecutable(path)) {
            throw new IOException(String.format("cannot delete file %s; missing permissions", path));
        }
    }

    @Override
    public void commit() throws IOException {
        Files.deleteIfExists(path);
    }

    @Override
    public void rollback() throws IOException {
        // in this case: do nothing
    }

}
