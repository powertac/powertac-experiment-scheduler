package org.powertac.rachma.file;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Builder
public class FileNode {

    @Getter
    private Path path;

    @Getter
    @Builder.Default
    private boolean isDirectory = false;

    @Getter
    @JsonIgnore
    private FileNode parent;

    @Getter
    @Builder.Default
    private List<FileNode> children = new ArrayList<>();

    public String getName() {
        return path.getFileName().toString();
    }

}
