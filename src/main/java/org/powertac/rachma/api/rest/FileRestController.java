package org.powertac.rachma.api.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.file.DownloadToken;
import org.powertac.rachma.file.DownloadTokenRepository;
import org.powertac.rachma.file.FileNode;
import org.powertac.rachma.file.FileTreeBuilder;
import org.powertac.rachma.game.GameRun;
import org.powertac.rachma.game.GameRunRepository;
import org.powertac.rachma.paths.PathProvider;
import org.powertac.rachma.paths.PathTranslator;
import org.powertac.rachma.security.JwtTokenService;
import org.powertac.rachma.user.User;
import org.powertac.rachma.user.UserProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("files")
public class FileRestController {

    @Value("${directory.local.base}")
    private String localBaseDir;

    private final GameRunRepository runRepository;
    private final PathProvider paths;
    private final PathTranslator pathTranslator;
    private final FileTreeBuilder fileTreeBuilder;
    private final JwtTokenService tokenService;
    private final UserProvider userProvider;
    private final DownloadTokenRepository tokenRepository;
    private final Logger logger;

    public FileRestController(GameRunRepository runRepository, PathProvider paths, PathTranslator pathTranslator,
                              FileTreeBuilder fileTreeBuilder, JwtTokenService tokenService, UserProvider userProvider,
                              DownloadTokenRepository tokenRepository) {
        this.runRepository = runRepository;
        this.paths = paths;
        this.pathTranslator = pathTranslator;
        this.fileTreeBuilder = fileTreeBuilder;
        this.tokenService = tokenService;
        this.userProvider = userProvider;
        this.tokenRepository = tokenRepository;
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
                logger.error("unable to remove files for run with id=" + id, e);
                return ResponseEntity.status(500).build();
            }
        }
    }

    @PostMapping("/download/{path}")
    public ResponseEntity<String> createDownloadToken(@PathVariable String path) {
        try {
            User user = userProvider.getCurrentUser();
            return ResponseEntity.ok(findOrCreateDownloadToken(user, path).getToken());
        } catch (Exception e) {
            logger.error("unable to create download token for path " + path, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/download")
    public ResponseEntity<Map<String, String>> createDownloadTokens(@RequestBody Set<String> paths) {
        try {
            User user = userProvider.getCurrentUser();
            Map<String, String> tokens = new HashMap<>();
            for (String path : paths) {
                DownloadToken download = findOrCreateDownloadToken(user, path);
                tokens.put(path, download.getToken());
            }
            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            logger.error("unable to create download tokens for batch", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @ResponseBody
    @GetMapping(value = "/download/{token}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public FileSystemResource downloadFileByToken(@PathVariable String token, HttpServletResponse response) {
        try {
            DownloadToken download = tokenService.getVerifiedDownloadToken(token);
            response.setHeader(
                "Content-disposition",
                String.format("attachment; filename=\"%s\"", Paths.get(download.getFilePath()).getFileName()));
            return new FileSystemResource(Paths.get(localBaseDir, download.getFilePath()));
        } catch (Exception e) {
            logger.error("unable to serve file for token=" + token, e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
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

    private DownloadToken findOrCreateDownloadToken(User user, String path) {
        Optional<DownloadToken> download = tokenRepository.findByUserAndFilePath(user, path);
        String token = tokenService.createDownloadToken(user, path);
        return download.orElseGet(() -> tokenRepository.save(DownloadToken.builder()
            .user(user)
            .filePath(path)
            .token(token)
            .build()));
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
