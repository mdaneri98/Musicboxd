package ar.edu.itba.paw.webapp.advice;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.AuthCUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UserControllerAdvice {

    @ModelAttribute(value = "loggedUser", binding = false)
    public User getLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof AuthCUserDetails pud) {
            return pud.getUser();
        }
        return null;
    }
}
