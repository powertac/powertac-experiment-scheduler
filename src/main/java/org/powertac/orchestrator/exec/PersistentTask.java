package org.powertac.orchestrator.exec;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.powertac.orchestrator.user.domain.User;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "task")
@SuperBuilder
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class PersistentTask implements Task {

    @Id
    @Getter
    @Column(length = 36)
    private String id;

    @Getter
    @ManyToOne
    private User creator;

    @Getter
    private Instant createdAt;

    @Getter
    @Setter
    private Instant start;

    @Getter
    @Setter
    private Instant end;

    @Getter
    @Setter
    private Integer priority;

    @Setter
    private boolean failed;

    public boolean hasFailed() {
        return failed;
    }

}
