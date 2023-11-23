package org.powertac.rachma.analysis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.powertac.rachma.baseline.Baseline;
import org.powertac.rachma.docker.ContainerTask;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.treatment.Treatment;

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
