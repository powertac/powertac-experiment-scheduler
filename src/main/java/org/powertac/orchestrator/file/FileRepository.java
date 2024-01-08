package org.powertac.orchestrator.file;

import org.powertac.orchestrator.game.Game;

public interface FileRepository {

    File findByRoleAndGame(FileRole role, Game game);

}
