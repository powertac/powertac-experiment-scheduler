package org.powertac.rachma.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.user.User;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Service
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenService tokenService;
    private final Logger logger;

    public JwtTokenFilter(JwtTokenService tokenService) {
        this.tokenService = tokenService;
        this.logger = LogManager.getLogger(JwtTokenFilter.class);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getToken(request);
            if (null != token) {
                User user = tokenService.getVerifiedUser(token);
                updateContext(user, request);
            }
        } catch (JWTVerificationException| TokenVerificationException e) {
            logger.error("JWT authentication failed", e);
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private String getToken(HttpServletRequest request) {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(header)) {
            return null;
        }
        final String[] headerParts = header.split(" ");
        if (headerParts.length > 1 && headerParts[0].trim().equals("Bearer") && StringUtils.hasText(headerParts[1])) {
            return headerParts[1].trim();
        }
        if (StringUtils.hasText(headerParts[0])) {
            return headerParts[0].trim();
        }
        return null;
    }

    private void updateContext(User user, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            user,
            null,
            user == null ? List.of() : user.getAuthorities());
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

}
