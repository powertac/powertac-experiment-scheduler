package org.powertac.rachma.api.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.file.FileNode;
import org.powertac.rachma.file.FileTreeBuilder;
import org.powertac.rachma.game.GameRun;
import org.powertac.rachma.game.GameRunRepository;
import org.powertac.rachma.paths.PathProvider;
import org.powertac.rachma.paths.PathTranslator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("files")
public class FileRestController {

    private final GameRunRepository runRepository;
    private final PathProvider paths;
    private final PathTranslator pathTranslator;
    private final FileTreeBuilder fileTreeBuilder;
    private final Logger logger;

    public FileRestController(GameRunRepository runRepository, PathProvider paths, PathTranslator pathTranslator, FileTreeBuilder fileTreeBuilder) {
        this.runRepository = runRepository;
        this.paths = paths;
        this.pathTranslator = pathTranslator;
        this.fileTreeBuilder = fileTreeBuilder;
        logger = LogManager.getLogger(FileRestController.class);
    }

    @GetMapping("/run/{id}")
    public ResponseEntity<FileNode> getRunFiles(@PathVariable String id) {
        GameRun run = runRepository.find(id);
        if (null == run) {
            logger.error(String.format("could not find run[%s]", id));
            return ResponseEntity.notFound().build();
        } else {
            try {
                return ResponseEntity.ok(fileTreeBuilder.build(paths.local().run(run).dir()));
            } catch (IOException e) {
                return ResponseEntity.status(500).build();
            }
        }
    }

    @DeleteMapping("/runs/{id}") // TODO : move to GameRunRestController
    public ResponseEntity<?> removeRunFiles(@PathVariable String id) {
        GameRun run = runRepository.find(id);
        if (null == run) {
            logger.error(String.format("could not find run[%s]", id));
            return ResponseEntity.notFound().build();
        } else {
            try {
                FileNode root = fileTreeBuilder.build(paths.local().run(run).dir());
                deleteFileTree(root, true);
                return ResponseEntity.ok().build();
            } catch (IOException e) {
                return ResponseEntity.status(500).build();
            }
        }
    }

    @GetMapping
    public ResponseEntity<String> readFile(@RequestParam("path") String path, Optional<Long> offset, Optional<Long> length) {
        try {
            // FIXME : CHECK PERMISSIONS FIRST (only per-game-scope allowed)
            Path filePath = pathTranslator.toLocal(Paths.get(path.replace("file://", "")));
            return ResponseEntity.ok().body(readFilePart(
                filePath,
                offset.orElse(0L),
                length.orElse(Long.MAX_VALUE)));
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    private String readFilePart(Path file, Long start, Long length) throws IOException {
        return Files.lines(file)
            .skip(start > 0 ? start - 1 : 0)
            .limit(length)
            .collect(Collectors.joining(System.lineSeparator()));
    }

    private void deleteFileTree(FileNode node, boolean isRoot) throws IOException {
        for (FileNode child : node.getChildren()) {
            deleteFileTree(child, false);
        }
        if (!isRoot) {
            Files.delete(node.getPath());
        }
    }

}
