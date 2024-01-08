package org.powertac.orchestrator.paths;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

@Component
public class HostPathSerializer extends StdSerializer<Path> {

    private final PathTranslator pathTranslator;

    public HostPathSerializer(PathTranslator pathTranslator) {
        super(Path.class);
        this.pathTranslator = pathTranslator;
    }

    @Override
    public void serialize(Path path, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(pathTranslator.toHost(path).toString());
    }
}
