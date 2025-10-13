package ar.edu.itba.paw.api.utils;

import ar.edu.itba.paw.api.models.Link;
import ar.edu.itba.paw.api.models.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for HATEOAS operations using UriBuilder to avoid code duplication
 */
@Component
public class HATEOASUtils {
    
    private static UriBuilder uriBuilder;
    
    @Autowired
    public HATEOASUtils(UriBuilder uriBuilder) {
        HATEOASUtils.uriBuilder = uriBuilder;
    }
    /**
     * Creates CRUD links for a resource using UriBuilder
     */
    public static void addCrudLinks(Resource<?> resource, String baseUrl, String resourcePath, Long id) {
        String uri = uriBuilder.buildResourceUri(baseUrl, resourcePath, id);
        
        resource.addSelfLink(uri);
        resource.addEditLink(uri);
        resource.addDeleteLink(uri);
        
        String collectionUri = uriBuilder.buildCollectionUri(baseUrl, resourcePath);
        resource.addCollectionLink(collectionUri);
    }
    
    /**
     * Creates CRUD links for a collection using UriBuilder
     */
    public static void addCollectionCrudLinks(Resource<?> resource, String baseUrl, String resourcePath) {
        String uri = uriBuilder.buildCollectionUri(baseUrl, resourcePath);
        
        resource.addSelfLink(uri);
        resource.addCreateLink(uri);
    }
    
    /**
     * Creates pagination links for a collection using UriBuilder
     */
    public static void addPaginationLinks(Resource<?> resource, String baseUrl, String resourcePath, 
                                        Integer currentPage, Integer totalPages, Integer pageSize) {
        if (currentPage == null || totalPages == null || pageSize == null) {
            return;
        }
        
        // First page link
        if (currentPage > 1) {
            String firstUri = uriBuilder.buildPaginatedUri(baseUrl, resourcePath, 1, pageSize);
            resource.addLink(Link.createLink(firstUri, "first", "First Page", "GET"));
        }
        
        // Previous page link
        if (currentPage > 1) {
            String prevUri = uriBuilder.buildPaginatedUri(baseUrl, resourcePath, currentPage - 1, pageSize);
            resource.addLink(Link.createLink(prevUri, "prev", "Previous Page", "GET"));
        }
        
        // Next page link
        if (currentPage < totalPages) {
            String nextUri = uriBuilder.buildPaginatedUri(baseUrl, resourcePath, currentPage + 1, pageSize);
            resource.addLink(Link.createLink(nextUri, "next", "Next Page", "GET"));
        }
        
        // Last page link
        if (currentPage < totalPages) {
            String lastUri = uriBuilder.buildPaginatedUri(baseUrl, resourcePath, totalPages, pageSize);
            resource.addLink(Link.createLink(lastUri, "last", "Last Page", "GET"));
        }
    }
    
    /**
     * Creates relationship links for related resources using UriBuilder
     */
    public static void addRelationshipLinks(Resource<?> resource, String baseUrl, String resourcePath, Long id, 
                                         String relatedResource, Long relatedId) {
        if (relatedId != null) {
            String uri = uriBuilder.buildResourceUri(baseUrl, relatedResource, relatedId);
            resource.addLink(Link.createLink(uri, "item", "View " + relatedResource, "GET"));
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
        
        return UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(serverName)
                .port(serverPort)
                .path(contextPath)
                .build()
                .toUriString();
    }
    
    /**
     * Gets the base URL from UriInfo
     */
    public static String getBaseUrl(UriInfo uriInfo) {
        URI baseUri = uriInfo.getBaseUri();
        return baseUri.toString();
    }
    
    /**
     * Creates a link with query parameters using UriComponentsBuilder
     */
    public static Link createLinkWithQuery(String baseUrl, String path, String rel, String title, 
                                          String method, Map<String, String> queryParams) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(path);
        
        // Add query parameters in pairs (key, value)
        if (queryParams != null && queryParams.size() > 0) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                builder.queryParam(entry.getKey(), entry.getValue());
            }
        }
        
        return Link.createLink(builder.toUriString(), rel, title, method);
    }
    
    /**
     * Creates search/filter links using UriComponentsBuilder
     */
    public static void addSearchLinks(Resource<?> resource, String baseUrl, String resourcePath) {
        Map<String, String> sortParams = new HashMap<>(Map.of("filter", "FIRST"));
        Map<String, String> searchParams = new HashMap<>(Map.of("search", ""));

        resource.addLink(createLinkWithQuery(baseUrl, resourcePath, "search", "Search resources", "GET", searchParams));
        resource.addLink(createLinkWithQuery(baseUrl, resourcePath, "sort", "Sort resources", "GET", sortParams));
    }
}
