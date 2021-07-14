package org.powertac.rachma.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.powertac.rachma.game.Game;

@AllArgsConstructor
public class File {

    @Getter
    private final String id;

    @Getter
    private final FileRole role;

    @Getter
    private final Game game;

}
