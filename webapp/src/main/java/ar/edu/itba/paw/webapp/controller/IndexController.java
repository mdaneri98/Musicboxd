package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.ApiInfoDTO;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(ApiUriConstants.API_BASE)
public class IndexController extends BaseController {

    @GET
    @Produces(CustomMediaType.API_INFO)
    public Response getApiInfo() {
        ApiInfoDTO apiInfo = new ApiInfoDTO(
                "Musicbox REST API",
                "1.0.0",
                "Music discovery and reviews"
        );

        // Build HATEOAS links
        apiInfo.setUsers(uriInfo.getBaseUriBuilder().path("users").build());
        apiInfo.setArtists(uriInfo.getBaseUriBuilder().path("artists").build());
        apiInfo.setAlbums(uriInfo.getBaseUriBuilder().path("albums").build());
        apiInfo.setSongs(uriInfo.getBaseUriBuilder().path("songs").build());
        apiInfo.setReviews(uriInfo.getBaseUriBuilder().path("reviews").build());

        return Response.ok(apiInfo).build();
    }
}
