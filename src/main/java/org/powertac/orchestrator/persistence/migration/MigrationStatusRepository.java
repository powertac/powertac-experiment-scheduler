package org.powertac.orchestrator.persistence.migration;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MigrationStatusRepository extends JpaRepository<MigrationStatus, Long> {

    boolean existsByNameAndSuccessTrue(String name);

}
