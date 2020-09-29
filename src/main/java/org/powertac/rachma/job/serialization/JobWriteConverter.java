package org.powertac.rachma.job.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.powertac.rachma.job.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class JobWriteConverter implements Converter<Job, Document> {

    private final ObjectMapper mapper;
    private final Logger logger;

    @Autowired
    public JobWriteConverter(ObjectMapper mapper) {
        this.mapper = mapper;
        this.logger = LogManager.getLogger(JobWriteConverter.class);
    }

    @Override
    public Document convert(Job job) {
        try {
            Document document = Document.parse(mapper.writeValueAsString(job));
            // TODO : possible bug: why is this called so many times?
            document.put("_id", job.getId());
            return document;
        }
        catch (JsonProcessingException e) {
            logger.error("could not convert job to document", e);
            return null;
        }
    }

}
