package org.powertac.orchestrator.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FileWatcher {

    private final WatchService watchService;
    private final Logger logger;

    private final Path filePath;
    private final Consumer<Path> callback;
    private final Set<WatchEvent.Kind> eventKinds;

    private final WatchKey watchKey;
    private Thread thread;

    private static Set<WatchEvent.Kind> defaultEventKinds() {
        Set<WatchEvent.Kind> eventKinds = new HashSet<>();
        eventKinds.add(StandardWatchEventKinds.ENTRY_CREATE);
        eventKinds.add(StandardWatchEventKinds.ENTRY_MODIFY);
        eventKinds.add(StandardWatchEventKinds.ENTRY_DELETE);
        eventKinds.add(StandardWatchEventKinds.OVERFLOW);
        return eventKinds;
    }

    @SuppressWarnings("unchecked")
    private static <T> WatchEvent<T> castEvent(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    public FileWatcher(Path filePath, Consumer<Path> callback) throws IOException {
        this(filePath, callback, defaultEventKinds());
    }

    public FileWatcher(Path filePath, Consumer<Path> callback, Set<WatchEvent.Kind> eventKinds) throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.logger = LogManager.getLogger(FileWatcher.class);
        this.filePath = filePath;
        this.callback = callback;
        this.eventKinds = eventKinds;
        this.watchKey = createWatchKey();
        this.thread = createWatchThread();
    }

    public void watch() {
        thread.start();
    }

    public void unwatch() {
        thread = null;
    }

    private WatchKey createWatchKey() throws IOException {
        return filePath.getParent().register(watchService, eventKinds.toArray(new WatchEvent.Kind[0]));
    }

    private Thread createWatchThread() {
        return new Thread(() -> {
            try {
                for (;;) {

                    WatchKey key = watchService.take();

                    if (null == thread) {
                        break;
                    }

                    if (key != watchKey) {
                        continue;
                    }

                    List<Path> paths = key.pollEvents().stream()
                        .map(FileWatcher::<Path>castEvent)
                        .map(WatchEvent::context)
                        .collect(Collectors.toList());

                    processPaths(paths);

                    if (!key.reset()) {
                        logger.error(String.format("file permission watcher for path '%s' could not be renewed", filePath));
                        break;
                    }
                }
            }
            catch (InterruptedException e) {
                logger.error(String.format("file permission watcher for path '%s' was interrupted", filePath), e);
            }
        });
    }

    private List<Path> extractPathsFromEvents(List<WatchEvent> events) {
        return events.stream()
            .map(FileWatcher::<Path>castEvent)
            .map(WatchEvent::context)
            .collect(Collectors.toList());
    }

    private void processPaths(List<Path> paths) {
        for (Path path : paths) {
            if (!shouldPathBeProcessed(path))
                continue;
            processPath(path);
        }
    }

    private void processPath(Path path) {
        callback.accept(path);
    }

    private boolean shouldPathBeProcessed(Path path) {
        return path.equals(filePath.getFileName());
    }

}
