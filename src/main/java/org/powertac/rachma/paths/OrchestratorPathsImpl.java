package org.powertac.rachma.paths;

import org.powertac.rachma.baseline.Baseline;
import org.powertac.rachma.baseline.OrchestratorBaselinePathProvider;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameRun;
import org.powertac.rachma.game.OrchestratorGamePathProvider;
import org.powertac.rachma.game.OrchestratorGameRunPathProvider;
import org.powertac.rachma.treatment.OrchestratorTreatmentPathProvider;
import org.powertac.rachma.treatment.Treatment;

import java.nio.file.Path;
import java.nio.file.Paths;

public class OrchestratorPathsImpl implements PathProvider.OrchestratorPaths {

    private final String basePath;

    public OrchestratorPathsImpl(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public Path games() {
        return Paths.get(basePath, "games");
    }

    @Override
    public GamePaths game(Game game) {
        return new OrchestratorGamePathProvider(this, game);
    }

    @Override
    public GameRunPaths run(GameRun run) {
        return new OrchestratorGameRunPathProvider(this, run);
    }

    @Override
    public Path baselines() {
        return Paths.get(basePath, "baselines");
    }

    @Override
    public BaselinePaths baseline(Baseline baseline) {
        return new OrchestratorBaselinePathProvider(this, baseline);
    }

    @Override
    public Path treatments() {
        return Paths.get(basePath, "treatments");
    }

    @Override
    public TreatmentPaths treatment(Treatment treatment) {
        return new OrchestratorTreatmentPathProvider(this, treatment);
    }

}
