package org.powertac.rachma.api.stomp;

import org.powertac.rachma.job.Job;
import org.powertac.rachma.PathProvider;
import org.powertac.rachma.util.FileWatcher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Service
@Deprecated
public class JobLogChangeMessenger {

    private final SimpMessagingTemplate template;
    private final PathProvider paths;
    private final Map<Job, FileWatcher> fileWatchers = new HashMap<>();

    public JobLogChangeMessenger(SimpMessagingTemplate template, PathProvider paths) {
        this.template = template;
        this.paths = paths;
    }

    public void register(Job job) throws IOException {
        FileWatcher logFileWatcher = createFileWatcher(job);
        fileWatchers.put(job, logFileWatcher);
        logFileWatcher.watch();
    }

    public void unregister(Job job) {
        if (!fileWatchers.containsKey(job)) {
            return;
        }
        fileWatchers.get(job).unwatch();
        fileWatchers.remove(job);
    }

    private FileWatcher createFileWatcher(Job job) throws IOException {
        return new FileWatcher(
            paths.getLocalLogFilePath(job),
            createMessagingCallback(job)
        );
    }

    private Consumer<Path> createMessagingCallback(Job job) {
        return (path) -> template.convertAndSend("/jobs", job);
    }

}
