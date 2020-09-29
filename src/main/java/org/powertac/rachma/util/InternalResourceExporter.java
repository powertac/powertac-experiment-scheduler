package org.powertac.rachma.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;

@Component
public class InternalResourceExporter {

    private final ResourcePatternResolver resourceResolver;
    private final Logger logger;

    public InternalResourceExporter(ResourcePatternResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
        this.logger = LogManager.getLogger(InternalResourceExporter.class);
    }

    public void exportDirectory(String resourceDirectory, String targetDirectory) throws IOException {

        Resource rootDirectory = resourceResolver.getResource(String.format("classpath:%s", resourceDirectory));
        Resource[] resources = resourceResolver.getResources(String.format("classpath:%s/**/*", resourceDirectory));

        for (Resource resource : resources) {

            Path targetFile = Paths.get(resource.getURL().getPath()
                .replace(rootDirectory.getURL().getPath(), targetDirectory));

            if (Files.exists(targetFile)) {
                logger.info(String.format("skipped existing resource '%s'", targetFile));
                continue;
            }

            if (isDirectory(resource)) {
                Files.createDirectories(targetFile);
                logger.info(String.format("created directory '%s'", targetFile));
                continue;
            }

            if (!Files.exists(targetFile.getParent())) {
                Files.createDirectories(targetFile.getParent());
                logger.info(String.format("created directory '%s'", targetFile.getParent()));
            }

            Files.copy(resource.getInputStream(), targetFile);

            if (shouldBeExecutable(targetFile)) {
                Files.setPosixFilePermissions(targetFile, PosixFilePermissions.fromString("rwxr--r--"));
            }

            logger.info(String.format("exported resource file '%s' to '%s'", resource.getURL().getPath(), targetFile));
        }
    }

    private boolean isDirectory(Resource resource) {
        /*
         * This is DEFINITELY a hacky and baaaad workaround. This workaround was implemented because this component
         * should be replaced as soon as possible by a detached broker provider service anyway.
         */
        try {
            resource.getInputStream().read();
            return false;
        }
        catch (IOException e) {
            return e.getMessage().equalsIgnoreCase("is a directory");
        }
    }

    private boolean shouldBeExecutable(Path file) throws IOException {
        /*
         * This is a workaround since there does not seem to be an elegant way to extract the resources' file
         * permissions when running the application from a jar file.
         *
         * Therefore at this point the application guesses if a file should be executable based on whether it includes
         * a shebang line.
         */
        String firstLine = getFirstLineOfFile(file).trim();
        return firstLine.startsWith("#!");
    }

    private String getFirstLineOfFile(Path file) throws IOException {
        BufferedReader reader = Files.newBufferedReader(file);
        String firstLine = reader.readLine();
        reader.close();
        return firstLine;
    }

}
