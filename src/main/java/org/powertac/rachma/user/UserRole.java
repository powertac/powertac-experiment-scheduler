package org.powertac.rachma.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserRole implements GrantedAuthority {

    @Id
    @Getter
    @Setter
    @Column(length = 128)
    private String name;

    @Override
    public String getAuthority() {
        return name;
    }

}
