package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.utils.Link;
import ar.edu.itba.paw.api.resources.Resource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.HATEOASUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.Map;

@Path(ApiUriConstants.API_BASE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IndexController extends BaseController {
    
    @GET
    public Response getApiInfo(@Context HttpServletRequest request, @Context UriInfo uriInfo) {
        String baseUrl = HATEOASUtils.getBaseUrl(request);

        Resource<Map<String, Object>> apiResource = new Resource<Map<String, Object>>() {
            @Override
            public Map<String, Object> getData() {
                Map<String, Object> apiInfo = new HashMap<>();
                apiInfo.put("name", "Musicbox REST API");
                apiInfo.put("version", "0.0.1");
                apiInfo.put("description", "Music discovery and reviews");
                return apiInfo;
            }
        };

            apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.USERS_BASE, "Users", "GET"));
            apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.ARTISTS_BASE, "Artists", "GET"));
            apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.ALBUMS_BASE, "Albums", "GET"));
            apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.SONGS_BASE, "Songs", "GET"));
            apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.REVIEWS_BASE, "Reviews", "GET"));

            
            // Add authentication links
            apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.LOGIN, "Login", "GET"));
            apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.REGISTER, "Register", "GET"));
            apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.LOGOUT, "Logout", "GET"));
        
        return buildResponse(apiResource);
    }

}