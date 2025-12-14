package ar.edu.itba.paw.webapp.utils;

import ar.edu.itba.paw.webapp.models.links.Link;
import ar.edu.itba.paw.webapp.models.links.managers.CollectionLinkManager;
import ar.edu.itba.paw.webapp.models.resources.CollectionResource;
import ar.edu.itba.paw.webapp.models.resources.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;



import javax.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
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
        
        String collectionUri = uriBuilder.buildCollectionUri(baseUrl, resourcePath, id);
        resource.addCollectionLink(collectionUri);
    }
    
    /**
     * Creates CRUD links for a collection using UriBuilder
     */
    public static void addCollectionCrudLinks(CollectionResource<?> resource, String baseUrl, String resourcePath, Long id, CollectionLinkManager collectionLinkManager) {
        String uri = uriBuilder.buildCollectionUri(baseUrl, resourcePath, id);
        
        resource.addSelfLink(uri);
        if (collectionLinkManager.getCreate()) resource.addCreateLink(uri);
        if (collectionLinkManager.getDelete()) resource.addDeleteLink(uri);
        if (collectionLinkManager.getEdit()) resource.addEditLink(uri);
        if (collectionLinkManager.getSearch()) addSearchLinks(resource, baseUrl, resourcePath);
        if (collectionLinkManager.getPagination()) addPaginationLinks(resource, baseUrl, resourcePath, resource.getCurrentPage(), resource.getTotalPages(), resource.getPageSize(), id);
    }
    
    /**
     * Creates pagination links for a collection using UriBuilder
     */
    public static void addPaginationLinks(Resource<?> resource, String baseUrl, String resourcePath, 
                                        Integer currentPage, Integer totalPages, Integer pageSize, Long id) {
        if (currentPage == null || totalPages == null || pageSize == null) {
            return;
        }
        
        // First page link
        if (currentPage > 1) {
            String firstUri = uriBuilder.buildPaginatedUri(baseUrl, resourcePath, 1, pageSize, id);
            resource.addLink(Link.createLink(firstUri, ControllerUtils.RELATION_PAGINATED_FIRST, "First Page", ControllerUtils.METHOD_GET));
        }
        
        // Previous page link
        if (currentPage > 1) {
            String prevUri = uriBuilder.buildPaginatedUri(baseUrl, resourcePath, currentPage - 1, pageSize, id);
            resource.addLink(Link.createLink(prevUri, ControllerUtils.RELATION_PAGINATED_PREV, "Previous Page", ControllerUtils.METHOD_GET));
        }
        
        // Next page link
        if (currentPage < totalPages) {
            String nextUri = uriBuilder.buildPaginatedUri(baseUrl, resourcePath, currentPage + 1, pageSize, id);
            resource.addLink(Link.createLink(nextUri, ControllerUtils.RELATION_PAGINATED_NEXT, "Next Page", ControllerUtils.METHOD_GET));
        }
        
        // Last page link
        if (currentPage < totalPages) {
            String lastUri = uriBuilder.buildPaginatedUri(baseUrl, resourcePath, totalPages, pageSize, id);
            resource.addLink(Link.createLink(lastUri, ControllerUtils.RELATION_PAGINATED_LAST, "Last Page", ControllerUtils.METHOD_GET));
        }
    }
    
    /**
     * Creates relationship links for related resources using UriBuilder
     */
    public static void addRelationshipLinks(Resource<?> resource, String baseUrl, String resourcePath, Long id, 
                                         String relatedResource, Long relatedId) {
        if (relatedId != null) {
            String uri = uriBuilder.buildResourceUri(baseUrl, relatedResource, relatedId);
            resource.addLink(Link.createLink(uri, ControllerUtils.RELATION_ITEM, "View " + relatedResource, ControllerUtils.METHOD_GET));
        }
    }

    public static void addImageLinks(Resource<?> resource, String baseUrl, String resourcePath, Long id) {
        String uri = uriBuilder.buildImageUri(baseUrl, id);
        Link link = new Link(uri, ControllerUtils.RELATION_IMAGE, "Image", MediaType.IMAGE_JPEG_VALUE, ControllerUtils.METHOD_GET);
        resource.addLink(link);
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
    
    public static String getBaseUrl(UriInfo uriInfo) {
        URI baseUri = uriInfo.getBaseUri();
        String baseUrl = baseUri.toString();
        
        // Remove trailing slash if present
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        
        return baseUrl;
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
        Map<String, String> filterParams = new HashMap<>(Map.of(ControllerUtils.FILTER_PARAM_NAME, ""));
        Map<String, String> searchParams = new HashMap<>(Map.of(ControllerUtils.SEARCH_PARAM_NAME, ""));

        resource.addLink(createLinkWithQuery(baseUrl, resourcePath, ControllerUtils.SEARCH_PARAM_NAME, "Search resources", ControllerUtils.METHOD_GET, searchParams));
        resource.addLink(createLinkWithQuery(baseUrl, resourcePath, ControllerUtils.FILTER_PARAM_NAME, "Filter resources", ControllerUtils.METHOD_GET, filterParams));
    }
}
