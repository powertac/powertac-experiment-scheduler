package org.powertac.rachma.job.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.powertac.rachma.job.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Deprecated
public class JobReadConverter implements Converter<Document, Job> {

    private final ObjectMapper mapper;
    private final Logger logger;
    private final JsonWriterSettings jsonWriterSettings;

    @Autowired
    public JobReadConverter(ObjectMapper mapper) {
        this.mapper = mapper;
        this.logger = LogManager.getLogger(JobReadConverter.class);
        this.jsonWriterSettings = generateJsonWriterSettings();
    }

    @Override
    public Job convert(Document document) {
        try {
            return mapper.readValue(document.toJson(jsonWriterSettings), Job.class);
        }
        catch (IOException e) {
            logger.error("could not convert document to job", e);
            return null;
        }
    }

    private static JsonWriterSettings generateJsonWriterSettings() {
        return JsonWriterSettings.builder()
            // long (int64) will be written as string and not as a composed object which makes deserialization
            // independent of bson specific formatting
            .int64Converter((value, writer) -> writer.writeNumber(value.toString()))
            .build();
    }

}
