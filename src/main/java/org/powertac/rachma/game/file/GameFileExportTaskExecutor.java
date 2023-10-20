package org.powertac.rachma.game.file;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.exec.PersistentTaskRepository;
import org.powertac.rachma.exec.TaskExecutor;
import org.powertac.rachma.file.GameFileExporter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class GameFileExportTaskExecutor implements TaskExecutor<GameFileExportTask> {

    private final PersistentTaskRepository taskRepository;
    private final GameFileExporter gameFileExporter;
    private final Logger logger;
    private final AtomicBoolean busy = new AtomicBoolean(false);

    public GameFileExportTaskExecutor(PersistentTaskRepository taskRepository, GameFileExporter gameFileExporter) {
        this.taskRepository = taskRepository;
        this.gameFileExporter = gameFileExporter;
        logger = LogManager.getLogger(GameFileExportTaskExecutor.class);
    }

    @Override
    public synchronized void exec(GameFileExportTask task) {
        try {
            busy.set(true);
            task.setStart(Instant.now());
            taskRepository.save(task);
            gameFileExporter.exportGames(task.getGames(), task.getTarget(), task.getBaseUri());
        } catch (Exception e) {
            logger.error("unable to export game files for task[id=" + task.getId() + "]");
            task.setFailed(true);
            taskRepository.save(task);
        } finally {
            task.setEnd(Instant.now());
            taskRepository.save(task);
            busy.set(false);
        }
    }

    @Override
    public synchronized boolean accepts(GameFileExportTask task) {
        return true;
    }

    @Override
    public synchronized boolean hasCapacity() {
        return !busy.get();
    }

}
