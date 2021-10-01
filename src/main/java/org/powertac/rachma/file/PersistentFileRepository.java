package org.powertac.rachma.file;

import org.powertac.rachma.game.Game;
import org.powertac.rachma.persistence.JpaFileRepository;
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
