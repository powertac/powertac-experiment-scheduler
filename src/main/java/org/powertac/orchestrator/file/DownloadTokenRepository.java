package org.powertac.orchestrator.file;

import org.powertac.orchestrator.user.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DownloadTokenRepository extends CrudRepository<DownloadToken, DownloadTokenId> {

    Optional<DownloadToken> findByToken(String token);
    Optional<DownloadToken> findByUserAndFilePath(User user, String filePath);

}
