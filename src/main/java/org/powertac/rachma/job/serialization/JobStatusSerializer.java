package org.powertac.rachma.job.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.powertac.rachma.job.JobStatus;

import java.io.IOException;
import java.math.BigDecimal;

@Deprecated
public class JobStatusSerializer extends StdSerializer<JobStatus> {

    public JobStatusSerializer() {
        super(JobStatus.class);
    }

    @Override
    public void serialize(JobStatus status, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (status != null) {
            gen.writeStartObject();
            // TODO : this BigDecimal conversion might lead to strange formatting problems
            BigDecimal start = null != status.getStart() ? new BigDecimal(status.getStart().toEpochMilli()) : null;
            BigDecimal end = null != status.getEnd() ? new BigDecimal(status.getEnd().toEpochMilli()) : null;
            gen.writeNumberField("start", start);
            gen.writeNumberField("end", end);
            gen.writeStringField("state", status.getState().toString());
            gen.writeEndObject();
        }
    }
}
