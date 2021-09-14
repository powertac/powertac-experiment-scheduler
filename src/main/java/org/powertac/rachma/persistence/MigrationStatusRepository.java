package org.powertac.rachma.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MigrationStatusRepository extends JpaRepository<MigrationStatus, Long> {

    boolean existsAllByNameAndSuccessTrue(String name);

}
