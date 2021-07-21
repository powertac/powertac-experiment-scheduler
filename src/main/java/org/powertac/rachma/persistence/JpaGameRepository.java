package org.powertac.rachma.persistence;

import org.powertac.rachma.game.Game;
import org.springframework.data.repository.CrudRepository;

public interface JpaGameRepository extends CrudRepository<Game, String> {}
