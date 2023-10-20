package org.powertac.rachma.game.file;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.powertac.rachma.exec.PersistentTaskDTO;

@Getter
@SuperBuilder
public class GameFileExportTaskDTO extends PersistentTaskDTO {

    private String baselineId;
    private String treatmentId;
    private String target;
    private String baseUri;

}
