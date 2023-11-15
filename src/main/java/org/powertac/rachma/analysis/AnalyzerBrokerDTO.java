package org.powertac.rachma.analysis;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AnalyzerBrokerDTO {

    private String id;
    private String name;
    private String version;

}
