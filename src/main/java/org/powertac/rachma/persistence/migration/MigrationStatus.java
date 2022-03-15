package org.powertac.rachma.persistence.migration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Instant;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class MigrationStatus {

    @Id
    @Getter
    @Setter
    @GeneratedValue
    private Long id;

    @Getter
    private String name;

    @Getter
    private Instant start;

    @Getter
    private Instant end;

    @Getter
    private boolean success;

    public MigrationStatus(String name) {
        this.name = name;
    }

    public static MigrationStatus start(Migration migration) {
        MigrationStatus status = new MigrationStatus(migration.getName());
        status.start = Instant.now();
        return status;
    }

    public void completeNow() {
        this.end = Instant.now();
        this.success = true;
    }

    public void failNow() {
        this.end = Instant.now();
        this.success = false;
    }

}
