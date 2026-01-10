package ar.edu.itba.paw.webapp.filter;

import ar.edu.itba.paw.models.AuthResult;
import ar.edu.itba.paw.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class BasicAuthFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicAuthFilter.class);
    private static final String AUTH_HEADER_TYPE = "Basic";
    private static final String JWT_ACCESS_HEADER = "X-JWT-Token";
    private static final String JWT_REFRESH_HEADER = "X-JWT-Refresh-Token";
    private static final String REALM = "Musicboxd";

    private final AuthService authService;

    public BasicAuthFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Skip if no Basic auth header
        if (authHeader == null || !authHeader.startsWith(AUTH_HEADER_TYPE + " ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract and decode credentials
            String credsBase64 = authHeader.substring(AUTH_HEADER_TYPE.length() + 1).trim();
            byte[] credsBytes = Base64.getDecoder().decode(credsBase64.getBytes(StandardCharsets.UTF_8));
            String credsDecoded = new String(credsBytes, StandardCharsets.UTF_8);

            int indexOfColon = credsDecoded.indexOf(':');
            if (indexOfColon < 0) {
                LOGGER.warn("Invalid Basic auth format - missing colon separator");
                addWwwAuthenticateHeader(response);
                filterChain.doFilter(request, response);
                return;
            }

            String username = credsDecoded.substring(0, indexOfColon);
            String password = credsDecoded.substring(indexOfColon + 1);

            LOGGER.info("Basic auth attempt for user: {}", username);

            // Authenticate using existing AuthService
            AuthResult authResult = authService.login(username, password);

            // Set JWT tokens in response headers
            response.setHeader(JWT_ACCESS_HEADER, authResult.getAccessToken());
            response.setHeader(JWT_REFRESH_HEADER, authResult.getRefreshToken());

            // Set SecurityContext
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

            LOGGER.info("Basic auth successful for user: {} (ID: {})",
                    authResult.getUser().getUsername(), authResult.getUser().getId());

        } catch (Exception e) {
            LOGGER.warn("Basic auth failed: {}", e.getMessage());
            addWwwAuthenticateHeader(response);
            // Don't set authentication - let the request continue unauthenticated
            // The security rules will handle 401/403 as appropriate
        }

        filterChain.doFilter(request, response);
    }

    private void addWwwAuthenticateHeader(HttpServletResponse response) {
        response.addHeader("WWW-Authenticate", AUTH_HEADER_TYPE + " realm=\"" + REALM + "\"");
    }
}
