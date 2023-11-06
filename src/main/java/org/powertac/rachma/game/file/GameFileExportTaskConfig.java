package org.powertac.rachma.game.file;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameFileExportTaskConfig {

    private String baselineId;
    private String treatmentId;
    private String target;
    private String baseUri;

}
