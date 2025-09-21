package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.models.Link;
import ar.edu.itba.paw.api.models.Resource;
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

        apiResource.addSelfLink(baseUrl + "/");
        apiResource.addLink(Link.collection(baseUrl + "/api/users", "users", "User management"));
        apiResource.addLink(Link.collection(baseUrl + "/api/artists", "artists", "Artist catalog"));
        apiResource.addLink(Link.collection(baseUrl + "/api/albums", "albums", "Album catalog"));
        apiResource.addLink(Link.collection(baseUrl + "/api/songs", "songs", "Song catalog"));
        apiResource.addLink(Link.collection(baseUrl + "/api/reviews", "reviews", "Review system"));
        apiResource.addLink(Link.collection(baseUrl + "/api/notifications", "notifications", "User notifications"));
        
        // Add authentication links
        apiResource.addLink(new Link(baseUrl + "/api/auth/login", "login", "User authentication", "application/json", "POST"));
        apiResource.addLink(new Link(baseUrl + "/api/auth/register", "register", "User registration", "application/json", "POST"));
        apiResource.addLink(new Link(baseUrl + "/api/auth/logout", "logout", "User logout", null, "POST"));
        
        return buildResponse(apiResource);
    }

}