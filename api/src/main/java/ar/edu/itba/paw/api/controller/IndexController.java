package ar.edu.itba.api.controller;

import ar.edu.itba.api.models.Link;
import ar.edu.itba.api.models.Resource;
import ar.edu.itba.api.utils.HATEOASUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class IndexController {
    
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
        apiResource.addLink(Link.collection(baseUrl + "/users", "users", "User management"));
        apiResource.addLink(Link.collection(baseUrl + "/artists", "artists", "Artist catalog"));
        apiResource.addLink(Link.collection(baseUrl + "/albums", "albums", "Album catalog"));
        apiResource.addLink(Link.collection(baseUrl + "/songs", "songs", "Song catalog"));
        apiResource.addLink(Link.collection(baseUrl + "/reviews", "reviews", "Review system"));
        apiResource.addLink(Link.collection(baseUrl + "/notifications", "notifications", "User notifications"));
        
        // Add authentication links
        apiResource.addLink(new Link(baseUrl + "/auth/login", "login", "User authentication", "application/json", "POST"));
        apiResource.addLink(new Link(baseUrl + "/auth/register", "register", "User registration", "application/json", "POST"));
        apiResource.addLink(new Link(baseUrl + "/auth/logout", "logout", "User logout", null, "POST"));
        
        return Response.ok(apiResource).build();
    }
}