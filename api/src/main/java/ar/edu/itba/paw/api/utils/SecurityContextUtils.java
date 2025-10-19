package ar.edu.itba.paw.api.utils;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.UserDao;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityContextUtils {
    
    /**
     * Get the current authenticated user from SecurityContext
     * @param userDao the UserDao to fetch user details
     * @return Optional containing the current user, empty if not authenticated
     */
    public static Optional<User> getCurrentUser(UserDao userDao) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getPrincipal())) {
            
            String username = authentication.getName();
            return userDao.findByUsername(username);
        }
        
        return Optional.empty();
    }
    
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
     * Check if current user is authenticated
     * @return true if user is authenticated
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() 
               && !"anonymousUser".equals(authentication.getPrincipal());
    }
}
