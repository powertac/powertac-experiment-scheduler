package org.powertac.rachma.file;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

    public Long getByteCount() {
        return new File(path.toString()).length();
    }

    public void delete() throws IOException {
        delete(false);
    }

    public void delete(boolean keepRoot) throws IOException {
        for (FileNode child : getChildren()) {
            child.delete();
        }
        if (!keepRoot) {
            Files.delete(getPath());
        }
    }

}
