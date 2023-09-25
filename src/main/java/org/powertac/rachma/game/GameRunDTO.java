package org.powertac.rachma.game;

import lombok.Builder;
import lombok.Getter;

@Builder
public class GameRunDTO {

    @Getter
    private String id;

    @Getter
    private Long start;

    @Getter
    private Long end;

    @Getter
    private String phase;

    @Getter
    private Boolean failed;

}
