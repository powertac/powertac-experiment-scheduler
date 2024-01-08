package org.powertac.orchestrator.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("security.jwt-secret")
    private String jwtSecret;

    @Bean
    public Algorithm jwtAlgorithm() {
        return Algorithm.HMAC384(jwtSecret);
    }

    @Bean
    public JWTVerifier jwtVerifier() {
        return JWT.require(jwtAlgorithm())
            .withIssuer("powertac")
            .build();
    }

}
