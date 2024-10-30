package ar.edu.itba.paw.webapp.config.resolver;

import ar.edu.itba.paw.webapp.auth.AuthCUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class UserLocaleResolver extends SessionLocaleResolver {

    private static final Logger logger = LoggerFactory.getLogger(UserLocaleResolver.class);

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        // Primero intentamos obtener el locale del usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthCUserDetails userDetails) {
            String preferredLanguage = userDetails.getUser().getPreferredLanguage();
            if (preferredLanguage != null && !preferredLanguage.isEmpty()) {
                Locale userLocale = Locale.forLanguageTag(preferredLanguage.replace('_', '-'));
                logger.info("Setting user preferred locale in session: {}", userLocale);
                // Guardamos explícitamente en la sesión
                setLocale(request, null, userLocale);
                return userLocale;
            }
        }

        return super.resolveLocale(request);
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        logger.info("Storing locale in session: {}", locale);
        request.getSession().setAttribute(LOCALE_SESSION_ATTRIBUTE_NAME, locale);
        super.setLocale(request, response, locale);
    }

}
