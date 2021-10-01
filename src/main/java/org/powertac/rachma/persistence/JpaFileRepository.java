package org.powertac.rachma.persistence;

import org.powertac.rachma.file.File;
import org.powertac.rachma.file.FileRole;
import org.powertac.rachma.game.Game;
import org.springframework.data.repository.CrudRepository;

public interface JpaFileRepository extends CrudRepository<File, String> {

    File findByRoleAndGame(FileRole role, Game game);

}
