package org.powertac.rachma.api.rest.v2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.baseline.Baseline;
import org.powertac.rachma.baseline.BaselineRepository;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.broker.BrokerRepository;
import org.powertac.rachma.treatment.*;
import org.powertac.rachma.user.RegistrationTokenCrudRepository;
import org.powertac.rachma.util.ID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v2/treatments")
public class TreatmentRestControllerV2 {

    private final BaselineRepository baselines;
    private final BrokerRepository brokers;
    private final TreatmentFactory treatmentFactory;
    private final TreatmentRepository treatmentRepository;
    private final Logger logger;
    private final RegistrationTokenCrudRepository registrationTokenCrudRepository;

    public TreatmentRestControllerV2(BaselineRepository baselines,
                                     BrokerRepository brokers, TreatmentFactory treatmentFactory,
                                     TreatmentRepository treatmentRepository,
                                     RegistrationTokenCrudRepository registrationTokenCrudRepository) {
        this.baselines = baselines;
        this.brokers = brokers;
        this.treatmentFactory = treatmentFactory;
        this.treatmentRepository = treatmentRepository;
        this.logger = LogManager.getLogger(TreatmentRestControllerV2.class);
        this.registrationTokenCrudRepository = registrationTokenCrudRepository;
    }

    @PostMapping("/")
    public ResponseEntity<Treatment> create(@RequestBody NewTreatmentDTO dto) {
        try {
            Optional<Baseline> baseline = baselines.findById(dto.getBaselineId());
            if (baseline.isPresent()) {
                Modifier modifier = dtoToModifier(dto.getName(), dto.getModifier());
                Treatment treatment = treatmentFactory.create(dto.getName(), baseline.get(), modifier);
                treatmentRepository.save(treatment);
                return ResponseEntity.ok(treatment);
            } else {
                logger.error("could not find baseline with id=" + dto.getBaselineId());
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            logger.error(e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}/")
    public ResponseEntity<Treatment> getById(@PathVariable String id) {
        Optional<Treatment> treatment = treatmentRepository.findById(id);
        if (treatment.isPresent()) {
            TreatmentDTO dto = treatmentToDto(treatment.get());
            return ResponseEntity.ok(treatment.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    private Modifier dtoToModifier(String treatmentName, NewModifierDTO dto) {
        if (dto.getType().equals(ModifierType.REPLACE_BROKER)) {
            Map<Broker, Broker> brokerMapping = new HashMap<>();
            for (Map.Entry<String, String> entry : ((ReplaceBrokerModifierConfigDTO) dto.getConfig()).getBrokerMapping().entrySet()) {
                Optional<Broker> original = brokers.findById(entry.getKey());
                Optional<Broker> replacement = brokers.findById(entry.getValue());
                if (original.isPresent() && replacement.isPresent()) {
                    brokerMapping.put(original.get(), replacement.get());
                } else {
                    throw new RuntimeException("could not find one or more brokers in broker mapping");
                }
            }
            return new ReplaceBrokerModifier(ID.gen(), treatmentName, brokerMapping);
        } else if (dto.getType().equals(ModifierType.PARAMETER_SET)) {
            Map<String, String> parameters = ((ParameterSetModifierConfigDTO) dto.getConfig()).getParameters();
            return new ParameterSetModifier(ID.gen(), treatmentName, parameters);
        } else {
            throw new RuntimeException("unknown modifier type");
        }
    }

    private TreatmentDTO treatmentToDto(Treatment treatment) {
        return TreatmentDTO.builder()
            .id(treatment.getId())
            .name(treatment.getName())
            .baselineId(treatment.getBaselineId())
            .modifier(modifierToDto(treatment.getModifier()))
            .gameIds(treatment.getGameIds())
            .createdAt(treatment.getCreatedAt())
            .build();
    }

    private ModifierDTO modifierToDto(Modifier modifier) {
        if (modifier instanceof ReplaceBrokerModifier) {
            Map<String, String> brokerIdMapping = new HashMap<>();
            ((ReplaceBrokerModifier) modifier)
                .getBrokerMapping()
                .forEach((original, replacement) -> brokerIdMapping.put(original.getId(), replacement.getId()));
            return new ModifierDTO(
                modifier.getId(),
                ModifierType.REPLACE_BROKER,
                new ReplaceBrokerModifierConfigDTO(brokerIdMapping));
        } else if (modifier instanceof ParameterSetModifier) {
            return new ModifierDTO(
                modifier.getId(),
                modifier.getType(),
                new ParameterSetModifierConfigDTO(((ParameterSetModifier) modifier).getParameters()));
        } else {
            throw new RuntimeException("unsupported modifier type");
        }
    }

}
