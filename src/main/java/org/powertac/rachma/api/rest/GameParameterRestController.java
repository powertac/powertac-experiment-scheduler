package org.powertac.rachma.api.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@RestController
@RequestMapping("game-parameters")
public class GameParameterRestController {

    private final ObjectMapper mapper;

    public GameParameterRestController(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping("/")
    public ResponseEntity<List<String>> getSupportedParams() {
        try {
            return ResponseEntity.ok(getParams());
        }
        catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    private List<String> getParams() throws IOException {
        InputStream paramFileStream = getClass().getClassLoader().getResourceAsStream("editable-server-properties.names.json");
        if (null == paramFileStream) {
            throw new IOException("cannot open file stream for editable server properties");
        }
        BufferedReader paramFileReader = new BufferedReader(new InputStreamReader(paramFileStream));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = paramFileReader.readLine()) != null) {
            content.append(line);
            content.append(System.lineSeparator());
        }
        return mapper.readValue(content.toString(), new TypeReference<>(){});
    }

}
