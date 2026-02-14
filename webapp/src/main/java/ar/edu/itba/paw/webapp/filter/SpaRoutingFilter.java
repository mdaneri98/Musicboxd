package ar.edu.itba.paw.webapp.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public class SpaRoutingFilter implements Filter {

    private ServletContext servletContext;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.servletContext = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        if (contextPath != null && !contextPath.isEmpty()) {
            path = path.substring(contextPath.length());
        }

        // Pass through API requests
        if (path.startsWith("/api/") || path.equals("/api")) {
            chain.doFilter(request, response);
            return;
        }

        // Pass through static resources
        if (path.startsWith("/_next/") || path.startsWith("/static/") || path.startsWith("/assets/")) {
            chain.doFilter(request, response);
            return;
        }

        // Pass through files with extensions (e.g. .js, .css, .png, .ico)
        String lastSegment = path.substring(path.lastIndexOf('/') + 1);
        if (lastSegment.contains(".")) {
            chain.doFilter(request, response);
            return;
        }

        // SPA page request — resolve correct Next.js HTML
        String htmlPath = resolveHtmlPath(path);

        InputStream htmlStream = servletContext.getResourceAsStream(htmlPath);
        if (htmlStream == null) {
            htmlStream = servletContext.getResourceAsStream("/index.html");
        }
        if (htmlStream == null) {
            chain.doFilter(request, response);
            return;
        }

        httpResponse.setContentType("text/html");
        httpResponse.setCharacterEncoding("UTF-8");

        try (InputStream in = htmlStream; OutputStream out = httpResponse.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    private String resolveHtmlPath(String path) {
        if (!path.endsWith("/")) {
            path = path + "/";
        }

        // Exact match: /login/ -> /login/index.html
        String exactPath = path + "index.html";
        if (servletContext.getResourceAsStream(exactPath) != null) {
            return exactPath;
        }

        // Dynamic match: /artists/1/ -> /artists/[artistId]/index.html
        String dynamicPath = resolveDynamicRoute(path);
        if (dynamicPath != null) {
            return dynamicPath;
        }

        return "/index.html";
    }

    private String resolveDynamicRoute(String path) {
        String trimmed = path.replaceAll("^/+|/+$", "");
        if (trimmed.isEmpty()) {
            return null;
        }
        String[] segments = trimmed.split("/");
        return matchSegments(segments, 0, "/");
    }

    private String matchSegments(String[] segments, int index, String currentPath) {
        if (index >= segments.length) {
            String htmlPath = currentPath + "index.html";
            if (servletContext.getResourceAsStream(htmlPath) != null) {
                return htmlPath;
            }
            return null;
        }

        String segment = segments[index];

        // Try exact directory match first
        String exactDir = currentPath + segment + "/";
        Set<String> contents = servletContext.getResourcePaths(exactDir);
        if (contents != null && !contents.isEmpty()) {
            String result = matchSegments(segments, index + 1, exactDir);
            if (result != null) {
                return result;
            }
        }

        // Try [param] directory match
        Set<String> siblings = servletContext.getResourcePaths(currentPath);
        if (siblings != null) {
            for (String sibling : siblings) {
                String dirName = sibling.substring(currentPath.length());
                if (dirName.endsWith("/")) {
                    dirName = dirName.substring(0, dirName.length() - 1);
                }
                if (dirName.startsWith("[") && dirName.endsWith("]")) {
                    String result = matchSegments(segments, index + 1, sibling);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public void destroy() {
    }
}
