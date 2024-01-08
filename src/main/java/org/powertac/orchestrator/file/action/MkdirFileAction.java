package org.powertac.orchestrator.file.action;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MkdirFileAction implements FileAction {

    private final Path path;

    public MkdirFileAction(Path path) {
        this.path = path;
    }

    @Override
    public void exec() throws IOException {
        Files.createDirectories(path);
    }

    @Override
    public void commit() throws IOException {
        // do nothing
    }

    @Override
    public void rollback() throws IOException {
        if (Files.walk(path).anyMatch(Files::isRegularFile)) {
            throw new IOException(String.format("cannot remove dir '%s'; it still contains files", path));
        }
        // deleting tree consisting only of directories; no files will be removed
        List<Path> dirs = Files.walk(path)
            .filter(Files::isDirectory)
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());
        for (Path dir : dirs) {
            Files.deleteIfExists(dir);
        }
    }

}
