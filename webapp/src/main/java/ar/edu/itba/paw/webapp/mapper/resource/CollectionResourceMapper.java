package ar.edu.itba.paw.webapp.mapper.resource;

import ar.edu.itba.paw.webapp.models.resources.CollectionResource;
import ar.edu.itba.paw.webapp.utils.HATEOASUtils;
import org.springframework.stereotype.Component;
import ar.edu.itba.paw.webapp.models.links.managers.CollectionLinkManager;

import java.util.List;

@Component
public class CollectionResourceMapper {

    /**
     * Creates a collection resource with HATEOAS links for CRUD operations
     */
    public <R> CollectionResource<R> createCollection(
            List<R> resources, Integer totalCount, Integer page, Integer size, String baseUrl, String resourcePath, CollectionLinkManager collectionLinkManager, Long id) {
        
        CollectionResource<R> collection = new CollectionResource<>(resources, totalCount.longValue(), size, page);
        
        // Add collection-level links
        HATEOASUtils.addCollectionCrudLinks(collection, baseUrl, resourcePath, id, collectionLinkManager);
        HATEOASUtils.addPaginationLinks(collection, baseUrl, resourcePath, page,collection.getTotalPages(), size, id);
        
        return collection;
    }

    public <R> CollectionResource<R> createCollection(
        List<R> resources, Integer totalCount, Integer page, Integer size, String baseUrl, String resourcePath, CollectionLinkManager collectionLinkManager) { // No id
        return createCollection(resources, totalCount, page, size, baseUrl, resourcePath, collectionLinkManager, null);
    }
}
