package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.models.links.Link;
import ar.edu.itba.paw.api.models.resources.Resource;
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
import ar.edu.itba.paw.api.utils.ControllerUtils;

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

            apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.USERS_BASE, ControllerUtils.ITEM_TYPE_USER, ControllerUtils.METHOD_GET));
            apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.ARTISTS_BASE, ControllerUtils.ITEM_TYPE_ARTIST, ControllerUtils.METHOD_GET));
            apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.ALBUMS_BASE, ControllerUtils.ITEM_TYPE_ALBUM, ControllerUtils.METHOD_GET));
            apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.SONGS_BASE, ControllerUtils.ITEM_TYPE_SONG, ControllerUtils.METHOD_GET));
            apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.REVIEWS_BASE, ControllerUtils.ITEM_TYPE_REVIEW, ControllerUtils.METHOD_GET));

            
            // Add authentication links
            apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.AUTH_BASE + ApiUriConstants.LOGIN, ControllerUtils.ACTION_LOGIN, ControllerUtils.METHOD_POST));
            apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.AUTH_BASE + ApiUriConstants.REGISTER, ControllerUtils.ACTION_REGISTER, ControllerUtils.METHOD_POST));
            apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.AUTH_BASE + ApiUriConstants.REFRESH, ControllerUtils.ACTION_REFRESH, ControllerUtils.METHOD_POST));
            apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.AUTH_BASE + ApiUriConstants.LOGOUT, ControllerUtils.ACTION_LOGOUT, ControllerUtils.METHOD_POST));
        
        return buildResponse(apiResource);
    }

}