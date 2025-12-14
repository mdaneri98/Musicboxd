package ar.edu.itba.paw.webapp.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class SecurityContextUtils {
    
    /**
     * Get the current authenticated user ID from SecurityContext
     * @return the user ID, or null if not authenticated
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getPrincipal())) {
            
            try {
                return Long.parseLong(authentication.getName());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        return null;
    }

    /**
     * Check if current user is moderator
     * @return true if user is moderator
     */
    public static boolean isModerator() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getPrincipal())
            && authentication.getAuthorities().contains(new SimpleGrantedAuthority("MODERATOR"));
    }
    
    /**
     * Check if current user is authenticated
     * @return true if user is authenticated
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() 
               && !"anonymousUser".equals(authentication.getPrincipal());
    }
}
