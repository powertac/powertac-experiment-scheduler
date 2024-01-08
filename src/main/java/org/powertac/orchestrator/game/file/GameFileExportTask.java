package org.powertac.orchestrator.game.file;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.powertac.orchestrator.baseline.Baseline;
import org.powertac.orchestrator.exec.PersistentTask;
import org.powertac.orchestrator.game.Game;
import org.powertac.orchestrator.treatment.Treatment;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import java.util.List;

@Entity
@SuperBuilder
@NoArgsConstructor
public class GameFileExportTask extends PersistentTask {

    @Getter
    @ManyToOne
    private Baseline baseline;

    @Getter
    @ManyToOne
    private Treatment treatment;

    @Getter
    private String target;

    @Getter
    private String baseUri;

    @Transient
    public List<Game> getGames() {
        if (baseline != null || treatment != null) {
            return baseline != null ? baseline.getGames() : treatment.getGames();
        } else {
            throw new RuntimeException("game file export task with id=" + getId() + " has no baseline or treatment configured");
        }
    }

}
