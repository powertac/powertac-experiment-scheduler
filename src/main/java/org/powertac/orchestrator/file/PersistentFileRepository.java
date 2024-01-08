package org.powertac.orchestrator.file;

import org.powertac.orchestrator.game.Game;
import org.powertac.orchestrator.persistence.JpaFileRepository;
import org.springframework.stereotype.Component;

@Component
public class PersistentFileRepository implements FileRepository {

    private final JpaFileRepository fileRepository;

    public PersistentFileRepository(JpaFileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public File findByRoleAndGame(FileRole role, Game game) {
        return fileRepository.findByRoleAndGame(role, game);
    }

}
