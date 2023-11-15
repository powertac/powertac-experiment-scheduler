package org.powertac.rachma.treatment;

import org.powertac.rachma.baseline.Baseline;
import org.powertac.rachma.paths.PathProvider;

import java.nio.file.Path;
import java.nio.file.Paths;

public class OrchestratorTreatmentPathProvider implements PathProvider.OrchestratorPaths.TreatmentPaths {

    private final PathProvider.OrchestratorPaths parent;
    private final Treatment treatment;

    public OrchestratorTreatmentPathProvider(PathProvider.OrchestratorPaths parent, Treatment treatment) {
        this.parent = parent;
        this.treatment = treatment;
    }

    @Override
    public Path dir() {
        return Paths.get(parent.treatments().toString(), treatment.getId());
    }

    @Override
    public Path artifacts() {
        return Paths.get(dir().toString(), "artifacts");
    }

}
