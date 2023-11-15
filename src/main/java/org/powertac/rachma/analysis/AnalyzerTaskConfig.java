package org.powertac.rachma.analysis;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AnalyzerTaskConfig {

    private String analyzerName;
    private String gameId;
    private String baselineId;
    private String treatmentId;

}
