package org.powertac.rachma.persistence.migration;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MigrationStatusRepository extends JpaRepository<MigrationStatus, Long> {

    boolean existsByNameAndSuccessTrue(String name);

}
