package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.utils.HATEOASUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.*;
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
    
    protected Response buildCreatedResponse(Object resource) {
        return Response.status(Response.Status.CREATED).entity(resource).build();
    }
    
    protected Response buildNoContentResponse() {
        return Response.noContent().build();
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
