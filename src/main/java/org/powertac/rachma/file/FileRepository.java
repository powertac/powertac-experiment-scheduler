package org.powertac.rachma.file;

import org.powertac.rachma.game.Game;

public interface FileRepository {

    File findByRoleAndGame(FileRole role, Game game);

}
