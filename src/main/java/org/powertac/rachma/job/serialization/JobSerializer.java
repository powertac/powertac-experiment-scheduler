package org.powertac.rachma.job.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.powertac.rachma.job.Job;
import org.powertac.rachma.powertac.broker.Broker;
import org.powertac.rachma.powertac.broker.BrokerImpl;
import org.powertac.rachma.broker.BrokerType;
import org.powertac.rachma.powertac.simulation.SimulationJob;
import org.powertac.rachma.util.IdGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JobSerializer extends StdSerializer<Job> {

    public JobSerializer() {
        super(Job.class);
    }

    @Override
    public void serialize(Job job, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (job != null) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("id", job.getId());
            jsonGenerator.writeStringField("name", job.getName());
            if (job instanceof SimulationJob) {
                writeSimulationJobFields((SimulationJob) job, jsonGenerator, serializerProvider);
            }
            serializerProvider.defaultSerializeField("status", job.getStatus(), jsonGenerator);
            jsonGenerator.writeEndObject();
        }
    }

    private void writeSimulationJobFields(SimulationJob job, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStringField("type", "SIMULATION");
        writeConfigField(job, jsonGenerator);
        writeBrokersField(jsonGenerator, job.getSimulationTask().getBrokers(), serializerProvider);
    }

    private void writeConfigField(SimulationJob job, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeObjectFieldStart("config");
        writeSimulationParametersField(job, jsonGenerator);
        jsonGenerator.writeEndObject();
    }

    private void writeSimulationParametersField(SimulationJob job, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeArrayFieldStart("simulation-parameters");
        for (Map.Entry<String, String> entry : job.getSimulationTask().getParameters().entrySet()) {
            writeParameterField(entry.getKey(), entry.getValue(), jsonGenerator);
        }
        jsonGenerator.writeEndArray();
    }

    private void writeParameterField(String parameter, String value, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("parameter", parameter);
        jsonGenerator.writeStringField("value", value);
        jsonGenerator.writeEndObject();
    }

    private void writeBrokersField(JsonGenerator jsonGenerator, Set<BrokerType> brokers, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeArrayFieldStart("brokers");
        Set<Broker> mockBrokers = createMockBrokers(brokers);
        for (Broker broker: mockBrokers) {
            serializerProvider.defaultSerializeValue(broker, jsonGenerator);
        }
        jsonGenerator.writeEndArray();
    }

    private Set<Broker> createMockBrokers(Set<BrokerType> brokerTypes) {
        return brokerTypes.stream()
            .map(this::createMockBroker)
            .collect(Collectors.toSet());
    }

    private Broker createMockBroker(BrokerType brokerType) {
        return new BrokerImpl(
            IdGenerator.generateId(),
            brokerType.getName(),
            brokerType
        );
    }

    @Deprecated
    private void writeLogField(JsonGenerator gen, Job job) throws IOException {
        File logFile = new File(job.getWorkDirectory().getLocalDirectory() + "/job." + job.getId() + ".log");
        if (logFile.exists()) {
            Path path = Paths.get(logFile.getCanonicalPath());
            Charset charset = StandardCharsets.UTF_8;
            String jobLog = new String(Files.readAllBytes(path), charset);
            gen.writeStringField("log", jobLog);
        }
    }

}
