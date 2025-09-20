package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.utils.HATEOASUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

public abstract class BaseController {
    
    @Context
    protected HttpServletRequest request;
    
    protected String getBaseUrl() {
        return HATEOASUtils.getBaseUrl(request);
    }
    
    protected Response buildResponse(Object resource) {
        return Response.ok(resource).build();
    }
    
    protected Response buildCreatedResponse(Object resource) {
        return Response.status(Response.Status.CREATED).entity(resource).build();
    }
    
    protected Response buildNoContentResponse() {
        return Response.noContent().build();
    }
}
