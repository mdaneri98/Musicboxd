package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.models.resources.CollectionResource;
import ar.edu.itba.paw.webapp.utils.HATEOASUtils;
import ar.edu.itba.paw.webapp.utils.PaginationHeadersBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.function.Supplier;

public abstract class BaseController {

    @Context
    protected HttpServletRequest request;

    @Context
    protected UriInfo uriInfo;

    @Autowired
    private ObjectMapper objectMapper;

    protected String getBaseUrl() {
        return HATEOASUtils.getBaseUrl(uriInfo);
    }

    protected Response buildResponse(Object resource) {
        try {
            String json = objectMapper.writeValueAsString(resource);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.serverError().entity("Serialization error").build();
        }
    }

    protected Response buildCreatedResponse(Object resource, URI location) {
        return Response.status(Response.Status.CREATED)
                .entity(resource)
                .header(HttpHeaders.LOCATION, location)
                .build();
    }

    protected URI buildResourceLocation(String resourcePath, Long resourceId) {
        return uriInfo.getBaseUriBuilder()
                .path(resourcePath)
                .path(String.valueOf(resourceId))
                .build();
    }

    protected URI buildNestedResourceLocation(String resourcePath, Long parentId, String subResourcePath, Long resourceId) {
        return uriInfo.getBaseUriBuilder()
                .path(resourcePath)
                .path(String.valueOf(parentId))
                .path(subResourcePath)
                .path(String.valueOf(resourceId))
                .build();
    }

    protected Response buildNoContentResponse() {
        return Response.noContent().build();
    }

    protected Response buildPaginatedResponse(CollectionResource<?> collection) {
        Response.ResponseBuilder responseBuilder = Response.ok(collection);
        
        PaginationHeadersBuilder.addPaginationHeaders(
            responseBuilder,
            uriInfo,
            collection.getCurrentPage(),
            collection.getPageSize(),
            collection.getTotalCount()
        );
        
        return responseBuilder.build();
    }


    // https://howtodoinjava.com/resteasy/jax-rs-resteasy-cache-control-with-etag-example/
    public static <T> Response buildResponseUsingEtag(Request request, Supplier<T> dtoSupplier) {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);

        final EntityTag eTag = new EntityTag(String.valueOf(dtoSupplier.get()));
        Response.ResponseBuilder response = request.evaluatePreconditions(eTag);

        if (response == null) {
            response = Response.ok(dtoSupplier.get()).tag(eTag);
            cacheControl.setNoStore(false);
        }

        return response.header(HttpHeaders.VARY, "Accept, Content-Type").cacheControl(cacheControl).build();
    }

    /**
     * Cache control
     * https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control
     */
    public static Response.ResponseBuilder setMaxAge(Response.ResponseBuilder responseBuilder, int maxAge) {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(maxAge);
        return responseBuilder.cacheControl(cacheControl);
    }

}
