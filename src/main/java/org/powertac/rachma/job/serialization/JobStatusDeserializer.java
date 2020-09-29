package org.powertac.rachma.job.serialization;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import org.powertac.rachma.job.JobState;
import org.powertac.rachma.job.JobStatus;
import org.powertac.rachma.job.JobStatusImpl;

import java.io.IOException;
import java.time.Instant;

public class JobStatusDeserializer extends StdNodeBasedDeserializer<JobStatus> {

    public JobStatusDeserializer() {
        super(JobStatus.class);
    }

    @Override
    public JobStatus convert(JsonNode jsonNode, DeserializationContext deserializationContext) throws IOException {
        Instant start = parseInstant(jsonNode.get("start"));
        Instant end = parseInstant(jsonNode.get("end"));
        JobState state = parseJobState(jsonNode.get("state"));
        return new JobStatusImpl(start, end, state);
    }

    private Instant parseInstant(JsonNode timeNode) {
        if (timeNode.isNull()) {
            return null;
        }
        return Instant.ofEpochMilli(timeNode.asLong());
    }

    private JobState parseJobState(JsonNode stateNode) {
        return JobState.valueOf(stateNode.asText());
    }

}
