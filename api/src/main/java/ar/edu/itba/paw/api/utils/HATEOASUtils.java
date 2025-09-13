package ar.edu.itba.api.utils;

import ar.edu.itba.api.models.Link;
import ar.edu.itba.api.models.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * Utility class for HATEOAS operations
 */
public class HATEOASUtils {
    
    /**
     * Creates a self link for a resource
     */
    public static Link createSelfLink(String baseUrl, String resourcePath, Long id) {
        return Link.self(baseUrl + resourcePath + "/" + id);
    }
    
    /**
     * Creates a self link for a collection
     */
    public static Link createCollectionSelfLink(String baseUrl, String resourcePath) {
        return Link.self(baseUrl + resourcePath);
    }
    
    /**
     * Creates CRUD links for a resource
     */
    public static void addCrudLinks(Resource<?> resource, String baseUrl, String resourcePath, Long id) {
        resource.addSelfLink(baseUrl + resourcePath + "/" + id);
        resource.addEditLink(baseUrl + resourcePath + "/" + id);
        resource.addDeleteLink(baseUrl + resourcePath + "/" + id);
        resource.addCollectionLink(baseUrl + resourcePath);
    }
    
    /**
     * Creates CRUD links for a collection
     */
    public static void addCollectionCrudLinks(Resource<?> resource, String baseUrl, String resourcePath) {
        resource.addSelfLink(baseUrl + resourcePath);
        resource.addCreateLink(baseUrl + resourcePath);
    }
    
    /**
     * Creates pagination links for a collection
     */
    public static void addPaginationLinks(Resource<?> resource, String baseUrl, String resourcePath, 
                                        Integer currentPage, Integer totalPages, Integer pageSize) {
        if (currentPage != null && totalPages != null) {
            // First page
            if (currentPage > 1) {
                resource.addLink(Link.first(baseUrl + resourcePath + "?page=1&size=" + pageSize));
            }
            
            // Previous page
            if (currentPage > 1) {
                resource.addLink(Link.prev(baseUrl + resourcePath + "?page=" + (currentPage - 1) + "&size=" + pageSize));
            }
            
            // Next page
            if (currentPage < totalPages) {
                resource.addLink(Link.next(baseUrl + resourcePath + "?page=" + (currentPage + 1) + "&size=" + pageSize));
            }
            
            // Last page
            if (currentPage < totalPages) {
                resource.addLink(Link.last(baseUrl + resourcePath + "?page=" + totalPages + "&size=" + pageSize));
            }
        }
    }
    
    /**
     * Creates relationship links for related resources
     */
    public static void addRelationshipLinks(Resource<?> resource, String baseUrl, String resourcePath, Long id, 
                                         String relatedResource, Long relatedId) {
        if (relatedId != null) {
            resource.addLink(Link.item(baseUrl + "/" + relatedResource + "/" + relatedId, 
                                     "item", "View " + relatedResource));
        }
    }
    
    /**
     * Gets the base URL from HttpServletRequest
     */
    public static String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        
        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);
        
        if ((scheme.equals("http") && serverPort != 80) || 
            (scheme.equals("https") && serverPort != 443)) {
            url.append(":").append(serverPort);
        }
        
        url.append(contextPath);
        
        return url.toString();
    }
    
    /**
     * Gets the base URL from UriInfo
     */
    public static String getBaseUrl(UriInfo uriInfo) {
        URI baseUri = uriInfo.getBaseUri();
        return baseUri.toString();
    }
    
    /**
     * Creates a link with query parameters
     */
    public static Link createLinkWithQuery(String href, String rel, String queryParams) {
        String fullHref = href;
        if (queryParams != null && !queryParams.isEmpty()) {
            fullHref += (href.contains("?") ? "&" : "?") + queryParams;
        }
        return new Link(fullHref, rel);
    }
    
    /**
     * Creates search/filter links
     */
    public static void addSearchLinks(Resource<?> resource, String baseUrl, String resourcePath) {
        resource.addLink(new Link(baseUrl + resourcePath + "/search", "search", "Search resources"));
        resource.addLink(new Link(baseUrl + resourcePath + "/filter", "filter", "Filter resources"));
    }
    
    /**
     * Creates action links for specific operations
     */
    public static void addActionLinks(Resource<?> resource, String baseUrl, String resourcePath, Long id) {
        // Example action links - customize based on your domain
        resource.addLink(new Link(baseUrl + resourcePath + "/" + id + "/follow", "follow", "Follow this resource", null, "POST"));
        resource.addLink(new Link(baseUrl + resourcePath + "/" + id + "/unfollow", "unfollow", "Unfollow this resource", null, "DELETE"));
        resource.addLink(new Link(baseUrl + resourcePath + "/" + id + "/like", "like", "Like this resource", null, "POST"));
        resource.addLink(new Link(baseUrl + resourcePath + "/" + id + "/unlike", "unlike", "Unlike this resource", null, "DELETE"));
    }
}
