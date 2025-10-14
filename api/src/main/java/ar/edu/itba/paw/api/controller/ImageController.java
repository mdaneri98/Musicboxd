package ar.edu.itba.paw.api.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.services.ImageService;

@Path(ApiUriConstants.IMAGES_BASE)
@Produces(MediaType.IMAGE_JPEG_VALUE)
@Consumes(MediaType.IMAGE_JPEG_VALUE)
public class ImageController extends BaseController {

    @Autowired
    private ImageService imageService;

    @GET
    @Path(ApiUriConstants.ID)
    public Response getImage(@PathParam("id") Long id) {
        Image image = imageService.findById(id);
        byte[] array = image.getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", MediaType.IMAGE_JPEG_VALUE);
        headers.set("Content-Disposition", String.format("inline; filename=\"image_%d.jpg\"", id));

        return Response.status(Response.Status.OK).entity(array).build();
    }
}
