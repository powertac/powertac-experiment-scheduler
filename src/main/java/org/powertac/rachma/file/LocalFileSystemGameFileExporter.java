package org.powertac.rachma.file;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameRun;
import org.powertac.rachma.paths.PathProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Component
public class LocalFileSystemGameFileExporter implements GameFileExporter {

    @Value("${directory.local.export}")
    private String exportBasePath;

    private final PathProvider paths;
    private final GameGroupManifestBuilder manifestBuilder;
    private final GameArchiveBuilder archiveBuilder;
    private final Logger logger;

    public LocalFileSystemGameFileExporter(PathProvider paths, GameGroupManifestBuilder manifestBuilder, GameArchiveBuilder archiveBuilder) {
        this.paths = paths;
        this.manifestBuilder = manifestBuilder;
        this.archiveBuilder = archiveBuilder;
        logger = LogManager.getLogger(GameFileExporter.class);
    }

    @Override
    public void exportGames(List<Game> games, String targetRoot, String hostUri) throws IOException {
        Path groupRootDir = Paths.get(exportBasePath, targetRoot);
        Files.createDirectories(groupRootDir);
        writeManifest(games, hostUri, targetRoot);
        createArchives(games, targetRoot);
    }

    private void writeManifest(List<Game> games, String hostUri, String folder) throws IOException {
        String manifest = manifestBuilder.buildManifest(games, hostUri, GameGroupManifestBuilderImpl.defaultDelimiter);
        Path manifestPath = Paths.get(exportBasePath, folder, "games.csv"); // TODO : make file name configurable
        Files.writeString(manifestPath, manifest, StandardCharsets.UTF_8);
    }

    private void createArchives(List<Game> games, String targetRoot) throws IOException {
        for (Game game : games) {
            Optional<GameRun> run = game.getSuccessfulRun();
            if (run.isPresent()) {
                archiveBuilder.buildArchive(run.get(), gameArchivePath(game, targetRoot));
            } else {
                logger.warn(String.format("skipping failed game(id=%s)", game.getId()));
            }
        }
    }

    private Path gameArchivePath(Game game, String targetRoot) {
        return Paths.get(exportBasePath, targetRoot, game.getId() + ".game.tar.gz");
    }

    @Deprecated
    private void exportGameLogs(GameRun run, String folder) throws IOException {
        String gameDir = gameDir(run.getGame(), folder);
        Files.createDirectories(Paths.get(gameDir));
        Path stateTarget = Paths.get(gameDir, String.format("%s.state", run.getGame().getId()));
        if (!Files.exists(stateTarget)) {
            Path stateSource = paths.local().run(run).state();
            Files.copy(stateSource, stateTarget);
            logger.info(String.format("copied %s to %s", stateSource, stateTarget));
        } else {
            logger.warn("skipping existing file " + stateTarget);
        }
        Path traceTarget = Paths.get(gameDir, String.format("%s.trace", run.getGame().getId()));
        if (!Files.exists(traceTarget)) {
            Path traceSource = paths.local().run(run).trace();
            Files.copy(traceSource, traceTarget);
            logger.info(String.format("moved %s to %s", traceSource, traceTarget));
        } else {
            logger.warn("skipping existing file " + traceTarget);
        }
    }

    @Deprecated
    private String gameDir(Game game, String folder) {
        return String.format("%s/%s/%s/%s", exportBasePath, folder, game.getId(), "log");
    }

}
