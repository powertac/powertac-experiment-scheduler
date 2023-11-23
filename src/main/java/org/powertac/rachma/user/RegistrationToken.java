package org.powertac.rachma.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.powertac.rachma.serialization.InstantToMillisSerializer;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.time.Instant;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationToken {

    @Id
    @GeneratedValue
    @Getter
    private Long id;

    @Getter
    private String token;

    @Getter
    @JsonSerialize(using = InstantToMillisSerializer.class)
    private Instant expirationDate;

    @Getter
    @OneToOne
    @JsonSerialize(using = UserIdSerializer.class)
    private User issuedBy;

    @Getter
    @JsonSerialize(using = InstantToMillisSerializer.class)
    private Instant issuedAt;

    @Getter
    @Setter
    @OneToOne
    @JsonSerialize(using = UserIdSerializer.class)
    private User claimedBy;

    @Getter
    @Setter
    @JsonSerialize(using = InstantToMillisSerializer.class)
    private Instant claimedAt;

}
