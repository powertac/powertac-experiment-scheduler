package org.powertac.rachma.game.file;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.powertac.rachma.baseline.Baseline;
import org.powertac.rachma.exec.PersistentTask;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.treatment.Treatment;

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
