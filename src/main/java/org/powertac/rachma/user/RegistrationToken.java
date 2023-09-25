package org.powertac.rachma.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.powertac.rachma.serialization.InstantToMillisSerializer;

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
