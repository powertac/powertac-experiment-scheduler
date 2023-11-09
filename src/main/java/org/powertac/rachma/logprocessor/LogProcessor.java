package org.powertac.rachma.logprocessor;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class LogProcessor {

    @Getter
    private String name;

    @Getter
    private String clazz;

    @Getter
    private String fileNamePattern;

}
