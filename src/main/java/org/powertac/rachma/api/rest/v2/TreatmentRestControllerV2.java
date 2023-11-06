package org.powertac.rachma.api.rest.v2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.baseline.Baseline;
import org.powertac.rachma.baseline.BaselineRepository;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.broker.BrokerRepository;
import org.powertac.rachma.exec.PersistentTaskDTO;
import org.powertac.rachma.exec.TaskDTOMapper;
import org.powertac.rachma.exec.TaskScheduler;
import org.powertac.rachma.game.file.GameFileExportTask;
import org.powertac.rachma.game.file.GameFileExportTaskConfig;
import org.powertac.rachma.game.file.GameFileExportTaskRepository;
import org.powertac.rachma.game.file.NewGameFileExportTaskDTO;
import org.powertac.rachma.treatment.*;
import org.powertac.rachma.user.UserNotFoundException;
import org.powertac.rachma.user.UserProvider;
import org.powertac.rachma.util.ID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v2/treatments")
public class TreatmentRestControllerV2 {

    private final BaselineRepository baselines;
    private final BrokerRepository brokers;
    private final TreatmentFactory treatmentFactory;
    private final TreatmentRepository treatmentRepository;
    private final TaskScheduler taskScheduler;
    private final TaskDTOMapper taskDTOMapper;
    private final UserProvider userProvider;
    private final GameFileExportTaskRepository exportTaskRepository;
    private final Logger logger;

    public TreatmentRestControllerV2(BaselineRepository baselines,
                                     BrokerRepository brokers, TreatmentFactory treatmentFactory,
                                     TreatmentRepository treatmentRepository, TaskScheduler taskScheduler,
                                     TaskDTOMapper taskDTOMapper, UserProvider userProvider,
                                     GameFileExportTaskRepository exportTaskRepository) {
        this.baselines = baselines;
        this.brokers = brokers;
        this.treatmentFactory = treatmentFactory;
        this.treatmentRepository = treatmentRepository;
        this.taskScheduler = taskScheduler;
        this.taskDTOMapper = taskDTOMapper;
        this.userProvider = userProvider;
        this.exportTaskRepository = exportTaskRepository;
        this.logger = LogManager.getLogger(TreatmentRestControllerV2.class);
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
            // TODO :   wrap creation in Transaction and only add games to queue once creation has been completed,
            //          otherwise remove persisted entities/files
            //          Check @Transactional in conjunction with rollback behaviours for details
            logger.error(e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}/")
    public ResponseEntity<TreatmentDTO> getById(@PathVariable String id) {
        Optional<Treatment> treatment = treatmentRepository.findById(id);
        if (treatment.isPresent()) {
            TreatmentDTO dto = treatmentToDto(treatment.get());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/")
    public ResponseEntity<Collection<TreatmentDTO>> getAll() {
        try {
            Set<Treatment> treatments = new HashSet<>();
            treatmentRepository.findAll().forEach(treatments::add);
            return ResponseEntity.ok(treatments.stream()
                .map(this::treatmentToDto)
                .collect(Collectors.toSet()));
        } catch (Exception e) {
            logger.error("unable to fetch treatments", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{id}/exports")
    public ResponseEntity<PersistentTaskDTO<GameFileExportTaskConfig>> export(@PathVariable String id, @RequestBody NewGameFileExportTaskDTO config) {
        Optional<Treatment> treatment = treatmentRepository.findById(id);
        if (treatment.isPresent()) {
            try {
                GameFileExportTask task = createExportTask(treatment.get(), config);
                taskScheduler.add(task);
                return ResponseEntity.ok(taskDTOMapper.toDTO(task));
            } catch (UserNotFoundException e) {
                logger.error("unable to create game file export task for treatment with id=" + id, e);
                return ResponseEntity.status(500).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/exports")
    public ResponseEntity<Collection<PersistentTaskDTO<GameFileExportTaskConfig>>> getExportTasks(@PathVariable String id) {
        try {
            Optional<Treatment> treatment = treatmentRepository.findById(id);
            if (treatment.isPresent()) {
                return ResponseEntity.ok(
                    exportTaskRepository.findAllByTreatment(treatment.get()).stream()
                        .map(taskDTOMapper::<GameFileExportTaskConfig>toDTO)
                        .collect(Collectors.toList()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("unable to get tasks for treatment with id=" + id, e);
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
            .createdAt(treatment.getCreatedAt().toEpochMilli())
            .config(treatment.getGames().get(0).getConfigDto())
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

    private GameFileExportTask createExportTask(Treatment treatment, NewGameFileExportTaskDTO dto) throws UserNotFoundException {
        return GameFileExportTask.builder()
            .id(ID.gen())
            .creator(userProvider.getCurrentUser())
            .createdAt(Instant.now())
            .treatment(treatment)
            .target(dto.getTarget())
            .baseUri(dto.getBaseUri())
            .build();
    }

}
