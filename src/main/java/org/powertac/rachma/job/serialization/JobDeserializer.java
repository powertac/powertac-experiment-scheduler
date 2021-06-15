package org.powertac.rachma.job.serialization;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import org.powertac.rachma.job.Job;
import org.powertac.rachma.job.JobStatus;
import org.powertac.rachma.powertac.bootstrap.BootstrapTask;
import org.powertac.rachma.powertac.broker.Broker;
import org.powertac.rachma.broker.BrokerType;
import org.powertac.rachma.powertac.simulation.SimulationJob;
import org.powertac.rachma.powertac.simulation.SimulationTask;
import org.powertac.rachma.resource.WorkDirectoryManager;
import org.powertac.rachma.util.DeserializationHelper;
import org.powertac.rachma.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class JobDeserializer extends StdNodeBasedDeserializer<Job> {

    private final WorkDirectoryManager workDirectoryManager;

    @Autowired
    public JobDeserializer(WorkDirectoryManager workDirectoryManager) {
        super(Job.class);
        this.workDirectoryManager = workDirectoryManager;
    }

    @Override
    public Job convert(JsonNode jsonNode, DeserializationContext context) throws IOException {
        if (jsonNode.get("type").asText().equals("SIMULATION")) {
            return createSimulationJob(jsonNode, context);
        }
        return null;
    }

    private Job createSimulationJob(JsonNode node, DeserializationContext context) throws IOException {
        SimulationJob simulationJob = new SimulationJob();
        simulationJob.setId(node.get("id").asText());
        simulationJob.setName(node.get("name").asText());
        simulationJob.setWorkDirectory(workDirectoryManager.create(simulationJob));
        simulationJob.setBootstrapTask(createBootstrapTask(simulationJob));
        simulationJob.setSimulationTask(createSimulationTask(simulationJob, node, context));
        simulationJob.setStatus(DeserializationHelper.defaultDeserialize(node.get("status"), JobStatus.class, context));
        return simulationJob;
    }

    private BootstrapTask createBootstrapTask(Job job) {
        return new BootstrapTask(IdGenerator.generateId(), job);
    }

    private SimulationTask createSimulationTask(Job job, JsonNode rootNode, DeserializationContext context) throws IOException {
        Set<BrokerType> brokerTypes = parseBrokerTypes(rootNode.get("brokers"), context);
        Map<String, String> parameters = parseParameters(rootNode);
        String bootstrapFile = rootNode.hasNonNull("bootstrapFile") ? rootNode.get("bootstrapFile").asText() : null;
        String seedFile = rootNode.hasNonNull("seedFile") ? rootNode.get("seedFile").asText() : null;
        return new SimulationTask(IdGenerator.generateId(), job, brokerTypes, parameters, bootstrapFile, seedFile);
    }

    private Set<BrokerType> parseBrokerTypes(JsonNode brokersNode, DeserializationContext context) throws IOException {
        return DeserializationHelper.parseAsSet(brokersNode, (node) -> {
            try {
                return parseBroker(node, context).getType();
            }
            catch (IOException e) {
                // TODO : this should probably be logged
                return null;
            }
        });
    }

    private Broker parseBroker(JsonNode node, DeserializationContext context) throws IOException {
        return DeserializationHelper.defaultDeserialize(node, Broker.class, context);
    }

    private Map<String, String> parseParameters(JsonNode node) {
        JsonNode configNode = node.get("config");
        if (configNode.has("simulation-parameters")) {
            JsonNode parametersNode = configNode.get("simulation-parameters");
            return DeserializationHelper.parseAsMap(parametersNode, (mapNode) ->
                Map.of(mapNode.get("parameter").asText(), mapNode.get("value").asText()));
        }
        return new HashMap<>();
    }

}
