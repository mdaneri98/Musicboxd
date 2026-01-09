package ar.edu.itba.paw.webapp.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpaRoutingServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String[][] DYNAMIC_ROUTES = {
        // Reviews
        { "^/reviews/(\\d+)/?$", "/reviews/[reviewId]/index.html" },
        
        // Songs
        { "^/songs/(\\d+)/?$", "/songs/[songId]/index.html" },
        { "^/songs/(\\d+)/edit-review/?$", "/songs/[songId]/edit-review/index.html" },
        { "^/songs/(\\d+)/reviews/?$", "/songs/[songId]/reviews/index.html" },
        
        // Albums
        { "^/albums/(\\d+)/?$", "/albums/[albumId]/index.html" },
        { "^/albums/(\\d+)/edit-review/?$", "/albums/[albumId]/edit-review/index.html" },
        { "^/albums/(\\d+)/reviews/?$", "/albums/[albumId]/reviews/index.html" },
        
        // Artists
        { "^/artists/(\\d+)/?$", "/artists/[artistId]/index.html" },
        { "^/artists/(\\d+)/edit-review/?$", "/artists/[artistId]/edit-review/index.html" },
        { "^/artists/(\\d+)/reviews/?$", "/artists/[artistId]/reviews/index.html" },
        
        // Users
        { "^/users/(\\d+)/?$", "/users/[userId]/index.html" },
        { "^/users/(\\d+)/followers/?$", "/users/[userId]/followers/index.html" },
        { "^/users/(\\d+)/following/?$", "/users/[userId]/following/index.html" },
    };

    // Static pages that exist as directories with index.html
    private static final String[] STATIC_PAGES = {
        "/",
        "/landing",
        "/login",
        "/register",
        "/search",
        "/music",
        "/music/view-all",
        "/settings",
        "/notifications",
        "/profile",
        "/profile/edit",
        "/forgot-password",
        "/reset-password",
        "/verify-email",
        "/moderator",
        "/moderator/music",
        "/403",
        "/404",
        "/500",
        "/reviews/not-found",
        "/users/not-found",
    };

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        handleRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        handleRequest(request, response);
    }

    private void handleRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        
        // Remove context path to get the actual path within the app
        String path = requestURI.substring(contextPath.length());
        
        // If path is empty, treat as root
        if (path.isEmpty()) {
            path = "/";
        }

        // Normalize path: remove trailing slash for comparison (except for root)
        String normalizedPath = path;
        if (!path.equals("/") && path.endsWith("/")) {
            normalizedPath = path.substring(0, path.length() - 1);
        }

        // 1. Check if it's a known static page
        for (String staticPage : STATIC_PAGES) {
            if (normalizedPath.equals(staticPage)) {
                // Forward to the static HTML file
                String forwardPath = staticPage.equals("/") ? "/index.html" : staticPage + "/index.html";
                request.getRequestDispatcher(forwardPath).forward(request, response);
                return;
            }
        }

        // 2. Check if it matches a dynamic route pattern
        for (String[] route : DYNAMIC_ROUTES) {
            Pattern pattern = Pattern.compile(route[0]);
            Matcher matcher = pattern.matcher(normalizedPath);
            if (matcher.matches()) {
                // Forward to the corresponding static HTML file
                request.getRequestDispatcher(route[1]).forward(request, response);
                return;
            }
        }

        // 3. Unknown route - forward to 404 page (never show Tomcat's error page)
        request.getRequestDispatcher("/404/index.html").forward(request, response);
    }
}
