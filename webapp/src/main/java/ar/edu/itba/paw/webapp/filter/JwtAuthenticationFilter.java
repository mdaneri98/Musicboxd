package ar.edu.itba.paw.webapp.filter;

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
    
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        
        // Skip JWT authentication for auth endpoints
        if (JwtUtils.isExcludedPath(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Clear any existing authentication to prevent stale data
        SecurityContextHolder.clearContext();
        
        String token = JwtUtils.extractTokenFromRequest(request);
        
        if (token != null) {
            try {
                if (jwtService.validateAccessToken(token)) {
                    Long userId = jwtService.extractUserId(token);
                    String username = jwtService.extractUsername(token);
                    String roles = jwtService.extractRoles(token);
                    
                    LOGGER.info("Processing JWT for path: {}, extracted roles: {}", requestPath, roles);
                    
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
                    
                    LOGGER.info("JWT authentication successful for user: {} (ID: {}) with roles: {}", username, userId, roles);
                    LOGGER.info("Authorities set in SecurityContext: {}", authorities);
                } else {
                    LOGGER.warn("Invalid JWT token");
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
