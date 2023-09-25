package org.powertac.rachma.api.rest.v2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.api.rest.BaselineController;
import org.powertac.rachma.baseline.*;
import org.powertac.rachma.broker.BrokerSetFactory;
import org.powertac.rachma.file.FileWriter;
import org.powertac.rachma.file.GameGroupManifestBuilder;
import org.powertac.rachma.file.GameGroupManifestBuilderImpl;
import org.powertac.rachma.game.GameConfig;
import org.powertac.rachma.game.generator.GameGeneratorConfig;
import org.powertac.rachma.game.generator.MultiplierGameGeneratorConfig;
import org.powertac.rachma.paths.PathProvider;
import org.powertac.rachma.util.ID;
import org.powertac.rachma.validation.exception.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/v2/baselines")
public class BaselineRestControllerV2 {

    private final BaselineFactory baselineFactory;
    private final BaselineRepository baselineRepository;
    private final BrokerSetFactory brokerSetFactory;
    private final BaselineDTOMapper baselineMapper;
    private final GameGroupManifestBuilder manifestBuilder;
    private final PathProvider paths;
    private final FileWriter fileWriter;
    private final Logger logger;

    public BaselineRestControllerV2(BaselineFactory factory, BaselineRepository baselineRepository,
                                    BrokerSetFactory brokerSetFactory, BaselineDTOMapper baselineMapper,
                                    GameGroupManifestBuilder manifestBuilder, PathProvider paths,
                                    FileWriter fileWriter) {
        this.baselineFactory = factory;
        this.baselineRepository = baselineRepository;
        this.brokerSetFactory = brokerSetFactory;
        this.baselineMapper = baselineMapper;
        this.manifestBuilder = manifestBuilder;
        this.paths = paths;
        this.fileWriter = fileWriter;
        this.logger = LogManager.getLogger(BaselineController.class);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaselineDTO> getById(@PathVariable String id) {
        try {
            Optional<Baseline> baseline = baselineRepository.findById(id);
            return baseline.map(b -> ResponseEntity.ok(baselineMapper.toDTO(b)))
                .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("could not load baseline " + id);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/")
    public ResponseEntity<Collection<BaselineDTO>> getBaselines() {
        try {
            return ResponseEntity.ok(StreamSupport.stream(baselineRepository.findAll().spliterator(), false)
                .map(baselineMapper::toDTO)
                .collect(Collectors.toList()));
        } catch (Exception e) {
            logger.error("could not load baselines", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<BaselineDTO> createBaseline(@RequestBody NewBaselineDTOv2 dto) {
        try {
            Baseline baseline = baselineFactory.generate(dto.getName(), parseGeneratorConfig(dto));
            baselineRepository.save(baseline);
            return ResponseEntity.ok().body(baselineMapper.toDTO(baseline));
        } catch (ValidationException e) {
            logger.error(e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("could not create baseline", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/manifest")
    public ResponseEntity<String> getManifest(@PathVariable String id) {
        try {
            Optional<Baseline> baseline = baselineRepository.findById(id);
            if (baseline.isPresent()) {
                Path manifestPath = paths.local().baseline(baseline.get()).manifest();
                return Files.exists(manifestPath)
                    ? ResponseEntity.ok(Files.readString(manifestPath))
                    : ResponseEntity.notFound().build();
            } else {
                logger.error("unable to get manifest; could not find baseline with id=" + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("unable to get manifest for baseline with id=" + id);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/manifest")
    public ResponseEntity<?> createManifest(@PathVariable String id) {
        try {
            Optional<Baseline> baseline = baselineRepository.findById(id);
            if (baseline.isPresent()) {
                String manifest = manifestBuilder.buildManifest(
                    baseline.get().getGames(), "", GameGroupManifestBuilderImpl.defaultDelimiter);
                PathProvider.OrchestratorPaths.BaselinePaths baselinePaths = paths.local().baseline(baseline.get());
                fileWriter.createDirectoryIfNotExists(baselinePaths.dir());
                fileWriter.write(baselinePaths.manifest(), manifest);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private GameGeneratorConfig parseGeneratorConfig(NewBaselineDTOv2 dto) throws ValidationException {
        GameConfig gameConfig = GameConfig.builder()
            .id(ID.gen())
            .brokers(brokerSetFactory.createFromIds(dto.getBrokerIds()))
            .parameters(dto.getParameters())
            .weather(dto.getWeather())
            .createdAt(Instant.now())
            .build();
        MultiplierGameGeneratorConfig generatorConfig = new MultiplierGameGeneratorConfig();
        generatorConfig.setId(ID.gen());
        generatorConfig.setGameConfig(gameConfig);
        generatorConfig.setMultiplier(dto.getSize());
        return generatorConfig;
    }

}
