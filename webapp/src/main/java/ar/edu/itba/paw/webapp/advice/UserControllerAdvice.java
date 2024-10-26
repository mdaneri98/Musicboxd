package ar.edu.itba.paw.webapp.advice;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.AuthCUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UserControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserControllerAdvice.class);

    @ModelAttribute(value = "loggedUser", binding = false)
    public User getLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null) {
            LOGGER.info("Authentication object: {}", auth);
            LOGGER.info("Authentication principal class: {}", auth.getPrincipal().getClass().getName());
            if (auth.getPrincipal() instanceof AuthCUserDetails pud) {
                User user = pud.getUser();
                LOGGER.info("User object: {}", user);
                if (user != null) {
                    LOGGER.info("User fields - username: {}, id: {}", user.getUsername(), user.getId());
                }
                return user;
            } else {
                LOGGER.warn("Principal is not an instance of AuthCUserDetails, it is: {}",
                        auth.getPrincipal().getClass().getName());
            }
        } else {
            LOGGER.warn("Authentication object is null");
        }
        return User.createAnonymous();
    }
}
