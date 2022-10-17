package org.powertac.rachma.file;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.powertac.rachma.game.GameRun;
import org.powertac.rachma.paths.PathProvider;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Component
public class GameArchiveBuilderImpl implements GameArchiveBuilder {

    private final PathProvider paths;

    public GameArchiveBuilderImpl(PathProvider paths) {
        this.paths = paths;
    }

    @Override
    public void buildArchive(GameRun run, Path output) throws IOException {
        try (OutputStream file = Files.newOutputStream(output);
             BufferedOutputStream buffer = new BufferedOutputStream(file);
             GzipCompressorOutputStream gzip = new GzipCompressorOutputStream(buffer);
             TarArchiveOutputStream tar = new TarArchiveOutputStream(gzip)) {
            for (Map.Entry<Path, Path> entry : getFileMap(run).entrySet()) {
                Path source = entry.getKey();
                Path target = entry.getValue();
                if (!Files.isRegularFile(source)) {
                    throw new IOException("only regular files are supported");
                }
                TarArchiveEntry tarEntry = new TarArchiveEntry(
                    source.toFile(),
                    target.toString());
                tar.putArchiveEntry(tarEntry);
                Files.copy(source, tar);
                tar.closeArchiveEntry();
            }
            tar.finish();
        }
    }

    private Map<Path, Path> getFileMap(GameRun run) {
        PathProvider.OrchestratorPaths.GameRunPaths runPaths = paths.local().run(run);
        Map<Path, Path> fileMap = new HashMap<>();
        Path statePath = runPaths.state();
        fileMap.put(statePath, Paths.get("/log", String.format("%s.state", run.getGame().getId())));
        Path tracePath = runPaths.trace();
        fileMap.put(tracePath, Paths.get("/log", String.format("%s.state", run.getGame().getId())));
        return fileMap;
    }

}
