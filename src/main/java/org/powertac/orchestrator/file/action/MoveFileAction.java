package org.powertac.orchestrator.file.action;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MoveFileAction implements FileAction {

    private final Path source;
    private final Path target;

    public MoveFileAction(Path source, Path target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public void exec() throws IOException {
        Files.copy(source, target);
    }

    @Override
    public void commit() throws IOException {
        Files.deleteIfExists(source);
    }

    @Override
    public void rollback() throws IOException {
        if (!Files.exists(source) && Files.exists(target)) {
            Files.copy(target, source);
        }
        if (Files.exists(target)) {
            Files.deleteIfExists(target);
        }
    }

}
