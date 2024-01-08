package org.powertac.orchestrator.api.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.orchestrator.game.Game;
import org.powertac.orchestrator.game.GameRepository;
import org.powertac.orchestrator.game.GameRun;
import org.powertac.orchestrator.game.GameRunRepository;
import org.powertac.orchestrator.paths.PathProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/storage")
public class StorageRestController {

    private final GameRepository gameRepository;
    private final GameRunRepository runRepository;
    private final PathProvider paths;
    private final Logger logger;

    public StorageRestController(GameRepository gameRepository, GameRunRepository runRepository, PathProvider paths) {
        this.gameRepository = gameRepository;
        this.runRepository = runRepository;
        this.paths = paths;
        logger = LogManager.getLogger(StorageRestController.class);
    }

    @GetMapping("/games/{id}")
    public ResponseEntity<Long> getGameFilesSize(@PathVariable("id") String gameId) {
        Game game = gameRepository.findById(gameId);
        if (null != game) {
            return ResponseEntity.ok(getSize(paths.local().game(game).dir()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/runs/{id}")
    public ResponseEntity<Long> getGameRunFilesSize(@PathVariable("id") String runId) {
        GameRun run = runRepository.find(runId);
        if (null != run) {
            return ResponseEntity.ok(getSize(paths.local().run(run).dir()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private Long getSize(Path path) {
        // https://stackoverflow.com/questions/2149785/get-size-of-folder-or-file/19877372#19877372
        final AtomicLong size = new AtomicLong(0);
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    size.addAndGet(attrs.size());
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    logger.warn("skipped: " + file + " (" + exc + ")");
                    // Skip folders that can't be traversed
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    if (exc != null) {
                        logger.warn("had trouble traversing: " + dir + " (" + exc + ")");
                    }
                    // Ignore errors traversing a folder
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new AssertionError("walkFileTree will not throw IOException if the FileVisitor does not");
        }
        return size.get();
    }

}
