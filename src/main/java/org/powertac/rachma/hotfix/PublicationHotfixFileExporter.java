package org.powertac.rachma.hotfix;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.baseline.Baseline;
import org.powertac.rachma.baseline.BaselineRepository;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameRun;
import org.powertac.rachma.paths.PathProvider;
import org.powertac.rachma.treatment.Treatment;
import org.powertac.rachma.treatment.TreatmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class PublicationHotfixFileExporter {

    @Value("${directory.local.base}")
    private String basePath;

    private final static String baselineId = "89ba9930-6d39-4708-82a7-ae97e75a55a8";
    private final static String pop1000TreatmentId = "66828607-ec03-4590-ae69-31920bf5a67c";
    private final static String pop20000TreatmentId = "6dec5d47-3540-43f5-a439-4f4a1edbe05e";

    private final BaselineRepository baselineRepository;
    private final TreatmentRepository treatmentRepository;
    private final PathProvider paths;
    private final Logger log;

    public PublicationHotfixFileExporter(BaselineRepository baselineRepository, TreatmentRepository treatmentRepository, PathProvider paths) {
        this.baselineRepository = baselineRepository;
        this.treatmentRepository = treatmentRepository;
        this.paths = paths;
        log = LogManager.getLogger("solar-lease-exporter");
    }

    public void exportLogFiles() {
        try {
            Files.createDirectories(Paths.get(exportPath()));
            exportBaselineFiles();
            exportTreatmentFiles(pop1000TreatmentId, "pop-1000");
            exportTreatmentFiles(pop20000TreatmentId, "pop-20000");
        } catch (Exception e) {
            log.error(e);
        }
    }

    private void exportBaselineFiles() throws IOException {
        Optional<Baseline> baseline = baselineRepository.findById(baselineId);
        if (baseline.isPresent()) {
            exportGames(baseline.get().getGames(), "baseline");
        } else {
            throw new IOException("could not find baseline");
        }
    }

    private void exportTreatmentFiles(String id, String folder) throws IOException {
        Optional<Treatment> treatment = treatmentRepository.findById(id);
        if (treatment.isPresent()) {
            exportGames(treatment.get().getGames(), folder);
        } else {
            throw new IOException("could not find " + folder);
        }
    }

    private void exportGames(List<Game> games, String folder) throws IOException {
        for (Game game : games) {
            exportGameLogs(game.getLatestSuccessfulRun(), folder);
        }
    }

    private void exportGameLogs(GameRun run, String folder) throws IOException {
        String gameDir = gameDir(run.getGame(), folder);
        Files.createDirectories(Paths.get(gameDir));
        Path state = Paths.get(gameDir, String.format("%s.state", run.getGame().getId()));
        if (!Files.exists(state)) {
            Files.copy(paths.local().run(run).state(), state);
        }
        Path trace = Paths.get(gameDir, String.format("%s.trace", run.getGame().getId()));
        if (!Files.exists(trace)) {
            Files.copy(paths.local().run(run).trace(), trace);
        }
    }

    private String gameDir(Game game, String folder) {
        return String.format("%s/%s/%s/%s", exportPath(), folder, game.getId(), "log");
    }

    private String exportPath() {
        return String.format("%s/export/solar-lease", basePath);
    }

}
