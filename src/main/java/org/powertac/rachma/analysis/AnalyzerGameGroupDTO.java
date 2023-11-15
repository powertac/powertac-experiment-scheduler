package org.powertac.rachma.analysis;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class AnalyzerGameGroupDTO {

    private String id;
    private String name;
    private AnalyzerGameGroupType type;
    private Set<AnalyzerGameDTO> games;

}
