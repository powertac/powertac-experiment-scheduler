package org.powertac.orchestrator.file.action;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class CopyFileAction implements FileAction {

    private final Path source;
    private final Path target;

    public CopyFileAction(Path source, Path target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public void exec() throws IOException {
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void commit() throws IOException {
        // in this case: do nothing
    }

    @Override
    public void rollback() throws IOException {
        Files.deleteIfExists(target);
    }

}
