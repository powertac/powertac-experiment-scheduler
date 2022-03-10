package testutils;

import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestHelper {

    public static String getResourcePath(String resourcePath) throws FileNotFoundException {
        return ResourceUtils.getFile(TestHelper.class.getResource(resourcePath)).getAbsolutePath();
    }

    public static String getContent(String resourcePath) throws IOException {
        String realPath = getResourcePath(resourcePath);
        return Files.readString(Paths.get(realPath));
    }

}
