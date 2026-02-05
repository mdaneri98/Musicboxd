package ar.edu.itba.paw.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.function.Supplier;

public abstract class BaseController {

    @Context
    protected HttpServletRequest request;

    @Context
    protected UriInfo uriInfo;


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
