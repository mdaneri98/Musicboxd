package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.form.UploadImageForm;
import ar.edu.itba.paw.api.mapper.resource.ImageResourceMapper;
import ar.edu.itba.paw.api.models.resources.ImageResource;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.ControllerUtils;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.services.ImageService;

@Path(ApiUriConstants.IMAGES_BASE)
public class ImageController extends BaseController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageResourceMapper imageResourceMapper;

    @GET
    @Path(ApiUriConstants.ID)
    @Produces("image/jpeg")
    public Response getImage(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        Image image = imageService.findById(id);
        byte[] array = image.getBytes();

        Response.ResponseBuilder responseBuilder = Response.ok(array).header(HttpHeaders.CONTENT_DISPOSITION, String.format("inline; filename=\"image_%d.jpg\"", id));
        return ControllerUtils.setMaxAge(responseBuilder, ControllerUtils.IMAGE_MAX_AGE).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadImage(@Valid @BeanParam final UploadImageForm uploadImageForm) {
        final Image image = imageService.create(uploadImageForm.getBytes());
        final ImageResource imageResource = imageResourceMapper.toResource(image, getBaseUrl());
        final URI imageUri = URI.create(getBaseUrl() + ApiUriConstants.IMAGES_BASE + "/" + image.getId());
        return Response.created(imageUri).entity(imageResource).build();
    }
}
