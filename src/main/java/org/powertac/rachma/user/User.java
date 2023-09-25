package org.powertac.rachma.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(using = UserSerializer.class)
public class User implements UserDetails {

    @Id
    @Getter
    @Column(length = 36)
    private String id;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private String token;

    @Getter
    @Setter
    private Instant tokenExpirationDate;

    @Setter
    private boolean enabled;

    @Getter
    @Setter
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<UserRole> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public boolean hasAuthority(String name) {
        for (GrantedAuthority authority : getAuthorities()) {
            if (authority.getAuthority().equals(name)) {
                return true;
            }
        }
        return false;
    }

}
