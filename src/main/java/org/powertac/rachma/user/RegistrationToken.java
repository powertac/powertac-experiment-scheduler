package org.powertac.rachma.user;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.Instant;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationToken {

    @Id
    @GeneratedValue
    private Long id;

    @Getter
    private String token;

    @Getter
    private Instant expirationDate;

    @Getter
    @OneToOne
    private User issuedBy;

    @Getter
    private Instant issuedAt;

    @Getter
    @Setter
    @OneToOne
    private User claimedBy;

    @Getter
    @Setter
    private Instant claimedAt;

}
