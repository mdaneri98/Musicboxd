package ar.edu.itba.paw.webapp.mapper.resource;

import java.util.List;

/**
 * Generic interface for mapping entities to resources
 * @param <E> Entity type
 * @param <R> Resource type
 */
public interface ResourceMapper<E, R> {
    
    /**
     * Converts a single entity to resource
     */
    R toResource(E entity, String baseUrl);
    
    /**
     * Converts a list of entities to resource list
     */
    List<R> toResourceList(List<E> entities, String baseUrl);
}
