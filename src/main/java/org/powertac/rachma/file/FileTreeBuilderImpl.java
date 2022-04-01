package org.powertac.rachma.file;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class FileTreeBuilderImpl implements FileTreeBuilder {

    public FileNode build(Path path) throws IOException {
        return build(path, null);
    }

    private FileNode build(Path path, FileNode parent) throws IOException {
        try (Stream<Path> children = Files.list(path)) {
            FileNode root = FileNode.builder()
                .path(path)
                .isDirectory(true)
                .parent(parent)
                .build();
            for (Path child : children.collect(Collectors.toList())) {
                if (Files.isDirectory(child)) {
                    root.getChildren().add(build(child, root));
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
