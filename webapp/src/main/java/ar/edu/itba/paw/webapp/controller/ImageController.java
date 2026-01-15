package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.ImageDTO;
import ar.edu.itba.paw.webapp.form.UploadImageForm;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path(ApiUriConstants.IMAGES_BASE)
public class ImageController extends BaseController {

    @Autowired
    private ImageService imageService;

    @GET
    @Path(ApiUriConstants.ID)
    @Produces("image/jpeg")
    public Response getImage(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        Image image = imageService.findById(id);
        byte[] array = image.getBytes();

        Response.ResponseBuilder responseBuilder = Response.ok(array)
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("inline; filename=\"image_%d.jpg\"", id));
        return setMaxAge(responseBuilder, ControllerUtils.IMAGE_MAX_AGE).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(CustomMediaType.IMAGE)
    public Response uploadImage(@Valid @BeanParam final UploadImageForm uploadImageForm) {
        final Image image = imageService.create(uploadImageForm.getBytes());

        ImageDTO imageDTO = new ImageDTO(image.getId());
        URI imageUri = uriInfo.getBaseUriBuilder()
                .path("images").path(String.valueOf(image.getId())).build();
        imageDTO.setSelf(imageUri);

        return Response.created(imageUri).entity(imageDTO).build();
    }
}
