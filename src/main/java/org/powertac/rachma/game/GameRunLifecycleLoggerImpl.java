package org.powertac.rachma.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.paths.PathProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class GameRunLifecycleLoggerImpl implements GameRunLifecycleLogger {

    private final PathProvider paths;
    private final Logger logger;

    public GameRunLifecycleLoggerImpl(PathProvider paths) {
        this.paths = paths;
        this.logger = LogManager.getLogger(GameRunLifecycleLogger.class);
    }

    @Override
    public void info(GameRun run, String message) {
        LogManager.getLogger(String.format("GameRun:%s", run.getId())).info(message);
        appendToLog(run, formatLogMessage("INFO", message));
    }

    @Override
    public void error(GameRun run, String message) {
        LogManager.getLogger(String.format("GameRun:%s", run.getId())).error(message);
        appendToLog(run, formatLogMessage("ERROR", message));
    }

    @Override
    public void error(GameRun run, String message, Throwable error) {
        error(run, message);
        appendToLog(run, formatError(error));
    }

    private String formatLogMessage(String level, String message) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"));
        return String.format("[%s] %s - %s", date, level, message);
    }

    private synchronized void appendToLog(GameRun run, String message) {
        Path logPath = paths.local().run(run).log();
        try {
            Path gameDirPath = paths.local().game(run.getGame()).dir();
            if (!Files.exists(gameDirPath)) {
                Files.createDirectories(gameDirPath);
            }
            if (!Files.exists(logPath)) {
                Files.createFile(logPath);
            }
            Files.writeString(logPath, message + "\n", StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.error(String.format("could not write to log file %s", logPath), e);
        }
    }

    private String formatError(Throwable error) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        error.printStackTrace(printWriter);
        printWriter.close();
        return stringWriter.toString();
    }

}
