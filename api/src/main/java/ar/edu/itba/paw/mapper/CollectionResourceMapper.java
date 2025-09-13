package ar.edu.itba.paw.mapper;

import ar.edu.itba.paw.models.CollectionResource;
import ar.edu.itba.paw.utils.HATEOASUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CollectionResourceMapper {

    /**
     * Creates a collection resource with HATEOAS links for CRUD operations
     */
    public <R> CollectionResource<R> createCollection(
            List<R> resources, Long totalCount, int page, int size, String baseUrl, String resourcePath) {
        
        CollectionResource<R> collection = new CollectionResource<>(resources, totalCount, size, page);
        
        // Add collection-level links
        HATEOASUtils.addCollectionCrudLinks(collection, baseUrl, resourcePath);
        HATEOASUtils.addPaginationLinks(collection, baseUrl, resourcePath, page,
                collection.getTotalPages(), size);
        HATEOASUtils.addSearchLinks(collection, baseUrl, resourcePath);
        
        return collection;
    }
}
