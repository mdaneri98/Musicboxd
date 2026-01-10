package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.ApiInfoDTO;
import ar.edu.itba.paw.webapp.models.links.Link;
import ar.edu.itba.paw.webapp.models.resources.ApiInfoResource;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.webapp.utils.HATEOASUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;

@Path(ApiUriConstants.API_BASE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IndexController extends BaseController {
    
    @GET
    public Response getApiInfo(@Context HttpServletRequest request, @Context UriInfo uriInfo) {
        String baseUrl = HATEOASUtils.getBaseUrl(request) + "/api";

        // Create API info DTO
        ApiInfoDTO apiInfo = new ApiInfoDTO(
            "Musicbox REST API",
            "0.0.1",
            "Music discovery and reviews"
        );
        
        ApiInfoResource apiResource = new ApiInfoResource(apiInfo);

        apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.USERS_BASE, ControllerUtils.ITEM_TYPE_USER, ControllerUtils.METHOD_GET));
        apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.ARTISTS_BASE, ControllerUtils.ITEM_TYPE_ARTIST, ControllerUtils.METHOD_GET));
        apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.ALBUMS_BASE, ControllerUtils.ITEM_TYPE_ALBUM, ControllerUtils.METHOD_GET));
        apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.SONGS_BASE, ControllerUtils.ITEM_TYPE_SONG, ControllerUtils.METHOD_GET));
        apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.REVIEWS_BASE, ControllerUtils.ITEM_TYPE_REVIEW, ControllerUtils.METHOD_GET));

        apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.AUTH_BASE + ApiUriConstants.LOGIN, ControllerUtils.ACTION_LOGIN, ControllerUtils.METHOD_POST));
        apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.AUTH_BASE + ApiUriConstants.REGISTER, ControllerUtils.ACTION_REGISTER, ControllerUtils.METHOD_POST));
        apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.AUTH_BASE + ApiUriConstants.REFRESH, ControllerUtils.ACTION_REFRESH, ControllerUtils.METHOD_POST));
        apiResource.addLink(Link.createLink(baseUrl + ApiUriConstants.AUTH_BASE + ApiUriConstants.LOGOUT, ControllerUtils.ACTION_LOGOUT, ControllerUtils.METHOD_POST));
        
        return buildResponse(apiResource);
    }

}