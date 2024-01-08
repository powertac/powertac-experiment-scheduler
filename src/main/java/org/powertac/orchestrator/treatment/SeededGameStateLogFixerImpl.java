package org.powertac.orchestrator.treatment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.orchestrator.game.Game;
import org.powertac.orchestrator.game.GameRun;
import org.powertac.orchestrator.paths.PathProvider;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class SeededGameStateLogFixerImpl implements SeededGameStateLogFixer {

    private final PathProvider paths;
    private final Logger logger;

    public SeededGameStateLogFixerImpl(PathProvider paths) {
        this.paths = paths;
        logger = LogManager.getLogger(SeededGameStateLogFixer.class);
    }

    @Override
    public void fixGameStateLog(Game game) {
        for (GameRun run : game.getRuns()) {
            Path stateLog = paths.local().run(run).state();
            try {
                logger.info("fixing seeded game log: " + stateLog);
                removeFirstSimEndMessage(stateLog);
                logger.info("fixed seeded game log: " + stateLog);
            } catch (Exception e) {
                logger.error("unable to fix seeded game log: " + stateLog);
            }
        }
    }

    private void removeFirstSimEndMessage(Path stateLog) throws IOException {
        boolean isFirst = true;
        Path backup = backupPath(stateLog);
        Files.move(stateLog, backup); // move original file to backup location
        String line;
        try (BufferedReader original = Files.newBufferedReader(backup); BufferedWriter fixed = Files.newBufferedWriter(stateLog)) {
          while ((line = original.readLine()) != null) {
              String trimmed = line.trim();
              if (trimmed.contains("SimEnd") && isFirst) {
                  isFirst = false;
                  continue;
              }
              fixed.write(line + System.getProperty("line.separator"));
          }
        }

    }

    private Path backupPath(Path stateLog) {
        return stateLog.resolveSibling(stateLog.getFileName() + ".backup");
    }

}
