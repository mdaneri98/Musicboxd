package ar.edu.itba.paw.webapp.filter;

import ar.edu.itba.paw.models.AuthResult;
import ar.edu.itba.paw.services.AuthService;
import ar.edu.itba.paw.webapp.utils.JwtUtils;
import ar.edu.itba.paw.services.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String JWT_ACCESS_HEADER = "X-JWT-Token";
    private static final String JWT_REFRESH_HEADER = "X-JWT-Refresh-Token";

    private final JwtService jwtService;
    private final AuthService authService;

    public JwtAuthenticationFilter(JwtService jwtService, AuthService authService) {
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();

        SecurityContextHolder.clearContext();
        
        String token = JwtUtils.extractTokenFromRequest(request);
        
        if (token != null) {
            try {
                // Check if it's a refresh token first
                if (jwtService.validateRefreshToken(token)) {
                    LOGGER.info("Refresh token detected, auto-refreshing tokens");
                    try {
                        AuthResult authResult = authService.refresh(token);

                        // Set new tokens in response headers
                        response.setHeader(JWT_ACCESS_HEADER, authResult.getAccessToken());
                        response.setHeader(JWT_REFRESH_HEADER, authResult.getRefreshToken());

                        // Set authentication from refresh result
                        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                        if (authResult.getUser().isModerator()) {
                            authorities.add(new SimpleGrantedAuthority("ROLE_MODERATOR"));
                        }

                        UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                authResult.getUser().getId().toString(),
                                null,
                                authorities
                            );
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        LOGGER.info("Token refresh successful for user: {} (ID: {})",
                            authResult.getUser().getUsername(), authResult.getUser().getId());
                    } catch (Exception e) {
                        LOGGER.warn("Token refresh failed: {}", e.getMessage());
                        response.addHeader("WWW-Authenticate", "Bearer realm=\"Musicboxd\"");
                    }
                } else if (jwtService.validateAccessToken(token)) {
                    // Regular access token processing
                    Long userId = jwtService.extractUserId(token);
                    String username = jwtService.extractUsername(token);
                    String roles = jwtService.extractRoles(token);

                    LOGGER.debug("Processing JWT for path: {}, extracted roles: {}", requestPath, roles);

                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                    if (roles != null && !roles.isEmpty()) {
                        String[] roleArray = roles.split(",");
                        for (String role : roleArray) {
                            String trimmedRole = role.trim();
                            if (!trimmedRole.startsWith("ROLE_")) {
                                trimmedRole = "ROLE_" + trimmedRole;
                            }
                            authorities.add(new SimpleGrantedAuthority(trimmedRole));
                        }
                    }

                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

                    // Set authentication in SecurityContext
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId.toString(), null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    LOGGER.debug("JWT authentication successful for user: {} (ID: {}) with roles: {}", username, userId, roles);
                } else {
                    LOGGER.warn("Invalid JWT token");
                    response.addHeader("WWW-Authenticate", "Bearer realm=\"Musicboxd\"");
                }
            } catch (Exception e) {
                LOGGER.error("Error processing JWT token", e);
            }
        } else {
            LOGGER.debug("No JWT token found in request");
        }
        
        filterChain.doFilter(request, response);
    }
}
