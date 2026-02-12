package ar.edu.itba.paw.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.*;
import java.util.function.Supplier;

public abstract class BaseController {

    @Context
    protected HttpServletRequest request;

    @Context
    protected UriInfo uriInfo;


    // https://howtodoinjava.com/resteasy/jax-rs-resteasy-cache-control-with-etag-example/
    public static <T> Response buildResponseUsingEtag(Request request, Supplier<T> dtoSupplier) {
        final T dto = dtoSupplier.get();
        final EntityTag eTag = new EntityTag(Integer.toString(dto.hashCode()));
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);

        Response.ResponseBuilder response = request.evaluatePreconditions(eTag);
        if (response != null) {
            return response.cacheControl(cacheControl).build();
        }

        return Response.ok(dto)
                .tag(eTag)
                .cacheControl(cacheControl)
                .build();
    }

    public static Response buildResponseUsingEtag(Request request, boolean exists) {
        final EntityTag eTag = new EntityTag(String.valueOf(Boolean.hashCode(exists)));
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);

        Response.ResponseBuilder response = request.evaluatePreconditions(eTag);
        if (response != null) {
            return response.cacheControl(cacheControl).build();
        }

        Response.ResponseBuilder builder = exists ? Response.noContent() : Response.status(Response.Status.NOT_FOUND);
        return builder.tag(eTag).cacheControl(cacheControl).build();
    }


}
