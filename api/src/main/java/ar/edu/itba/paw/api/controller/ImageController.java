package ar.edu.itba.paw.api.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Base64;

import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.dtos.ImageDTO;
import ar.edu.itba.paw.services.ImageService;

@Path(ApiUriConstants.IMAGES_BASE)
@Produces("image/jpeg")
@Consumes(MediaType.APPLICATION_JSON)
public class ImageController extends BaseController {

    @Autowired
    private ImageService imageService;

    @GET
    @Path(ApiUriConstants.ID)
    public Response getImage(@PathParam("id") Long id) {
        Image image = imageService.findById(id);
        byte[] array = image.getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "image/jpeg");
        headers.set("Content-Disposition", String.format("inline; filename=\"image_%d.jpg\"", id));

        return Response.status(Response.Status.OK).entity(array).header(getBaseUrl(), headers).build();
    }

    @POST
    public Response uploadImage(@Valid ImageDTO imageDTO) {
        byte[] bytes = Base64.getDecoder().decode(imageDTO.getBase64());
        final Image image = imageService.create(bytes);
        return buildCreatedResponse(image);
    }
}
