package org.powertac.orchestrator.file;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class FileTreeBuilderImpl implements FileTreeBuilder {

    public FileNode build(Path path) throws IOException {
        return build(path, null, Integer.MAX_VALUE);
    }

    @Override
    public FileNode build(Path path, int depth) throws IOException {
        return build(path, null, depth);
    }

    private FileNode build(Path path, FileNode parent, int depth) throws IOException {
        try (Stream<Path> children = Files.list(path)) {
            FileNode root = FileNode.builder()
                .path(path)
                .isDirectory(true)
                .parent(parent)
                .build();
            for (Path child : children.collect(Collectors.toList())) {
                if (Files.isDirectory(child) && depth > 0) {
                    root.getChildren().add(build(child, root, depth - 1));
                } else if (Files.isRegularFile(child)) {
                    root.getChildren().add(FileNode.builder()
                        .path(child)
                        .parent(root)
                        .build());
                }
            }
            return root;
        }
    }

}
