package org.powertac.rachma;

import org.powertac.rachma.job.Job;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Deprecated
public class PathProvider {

    public Path getLocalLogFilePath(Job job) {
        return Paths.get(formatLogFilePath(job.getWorkDirectory().getLocalDirectory(), job.getId()));
    }

    public Path getHostLogFilePath(Job job) {
        return Paths.get(formatLogFilePath(job.getWorkDirectory().getHostDirectory(), job.getId()));
    }

    private String formatLogFilePath(String directory, String jobId) {
        return directory + File.separator + "job." + jobId + ".log";
    }

}
