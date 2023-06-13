package org.powertac.rachma.logprocessor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.powertac.rachma.docker.ContainerTask;
import org.powertac.rachma.game.Game;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.util.Set;

@Entity
@SuperBuilder
@NoArgsConstructor
public class LogProcessorTask extends ContainerTask {

    @Getter
    @ManyToOne
    private Game game;

    @Getter
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> processorIds;

}
