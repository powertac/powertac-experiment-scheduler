package org.powertac.orchestrator.analysis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.powertac.orchestrator.baseline.Baseline;
import org.powertac.orchestrator.docker.ContainerTask;
import org.powertac.orchestrator.game.Game;
import org.powertac.orchestrator.treatment.Treatment;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzerTask extends ContainerTask {

    @Getter
    private String analyzerName;

    @Getter
    @ManyToOne
    private Game game;

    @Getter
    @ManyToOne
    private Baseline baseline;

    @Getter
    @ManyToOne
    private Treatment treatment;

}
