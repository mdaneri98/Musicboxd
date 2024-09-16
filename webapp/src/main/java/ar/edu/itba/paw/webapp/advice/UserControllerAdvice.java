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
            if (auth.getPrincipal() instanceof AuthCUserDetails pud) {
                LOGGER.info("Logged-in user: {}", pud.getUser());
                return pud.getUser();
            } else {
                LOGGER.warn("Principal is not an instance of AuthCUserDetails");
            }
        } else {
            LOGGER.warn("Authentication object is null");
        }
        return null;
    }
}
