package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.utils.HATEOASUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public abstract class BaseController {
    
    @Context
    protected HttpServletRequest request;

    @Autowired
    private ObjectMapper objectMapper;

    protected String getBaseUrl() {
        return HATEOASUtils.getBaseUrl(request);
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
}
