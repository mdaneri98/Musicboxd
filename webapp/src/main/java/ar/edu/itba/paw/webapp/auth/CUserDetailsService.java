package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.NotificationService;
import ar.edu.itba.paw.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;

@Service
public class CUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService us;

    @Autowired
    private NotificationService ns;

    @Autowired
    private static final Logger LOGGER = LoggerFactory.getLogger(CUserDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = us.findByUsername(username).orElseThrow(() -> {
            LOGGER.info("Intento de acceso con usuario no existente: " + username);
            return new UsernameNotFoundException("User not found");
        });

        // Validación adicional
        if (user.getUsername() == null || user.getId() == null) {
            LOGGER.error("Usuario encontrado pero con campos críticos nulos: {}", user);
            throw new UsernameNotFoundException("Invalid user state");
        }

        final Collection<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (user.isModerator()) authorities.add(new SimpleGrantedAuthority("ROLE_MODERATOR"));
        user.setUnreadNotificationCount(ns.getUnreadCount(user.getId()));
        AuthCUserDetails userDetails = new AuthCUserDetails(user, authorities);

        // Validación después de crear AuthCUserDetails
        if (userDetails.getUser() == null || userDetails.getUser().getUsername() == null) {
            LOGGER.error("AuthCUserDetails creado con usuario inválido");
            throw new UsernameNotFoundException("Invalid user state after AuthCUserDetails creation");
        }

        return userDetails;
    }

}


