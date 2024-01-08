package org.powertac.orchestrator.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class JsonListLoader {

    private final ObjectMapper mapper;

    public JsonListLoader(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T> List<T> loadResource(String resourcePath) throws IOException {
        InputStream paramFileStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        BufferedReader paramFileReader = new BufferedReader(new InputStreamReader(paramFileStream));

        StringBuilder content = new StringBuilder();
        String line;

        while ((line = paramFileReader.readLine()) != null) {
            content.append(line);
            content.append(System.lineSeparator());
        }

        return mapper.readValue(content.toString(), new TypeReference<List<T>>(){});
    }

}
