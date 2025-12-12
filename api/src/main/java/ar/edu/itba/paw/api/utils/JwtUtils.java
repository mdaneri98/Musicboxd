package ar.edu.itba.paw.api.utils;

import javax.servlet.http.HttpServletRequest;

public class JwtUtils {
    
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    
    /**
     * Extract token from Authorization header
     * @param request the HTTP request
     * @return the token without "Bearer " prefix, or null if not found
     */
    public static String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(HEADER_STRING);
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            return authHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
    
    /**
     * Check if request path should be excluded from JWT authentication
     * @param path the request path
     * @return true if path should be excluded
     */
    public static boolean isExcludedPath(String path) {
        return path != null && (
            path.startsWith(ApiUriConstants.AUTH_BASE) ||
            path.startsWith(ApiUriConstants.EMAIL_BASE)
        );
    }
}