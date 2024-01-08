package org.powertac.orchestrator.persistence;

import org.powertac.orchestrator.file.File;
import org.powertac.orchestrator.file.FileRole;
import org.powertac.orchestrator.game.Game;
import org.springframework.data.repository.CrudRepository;

public interface JpaFileRepository extends CrudRepository<File, String> {

    File findByRoleAndGame(FileRole role, Game game);

}
