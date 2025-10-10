package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.mapper.CommentResourceMapper;
import ar.edu.itba.paw.api.models.CommentResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.models.dtos.CommentDTO;
import ar.edu.itba.paw.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(ApiUriConstants.COMMENTS_BASE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommentController extends BaseController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentResourceMapper commentResourceMapper;

    @GET
    @Path("/{id:\\d+}")
    public Response getComment(@PathParam("id") Long id) {
        CommentDTO commentDTO = commentService.findById(id);
        CommentResource commentResource = commentResourceMapper.toResource(commentDTO, getBaseUrl());
        return buildResponse(commentResource);
    }

    @POST
    public Response createComment(@Valid CommentDTO commentDTO) {
        // TODO: Obtener userId del contexto de seguridad
        if (commentDTO.getReviewId() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Review ID is required")
                    .build();
        }

        CommentDTO responseDTO = commentService.create(commentDTO);
        CommentResource commentResource = commentResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildCreatedResponse(commentResource);
    }

    @PUT
    @Path("/{id:\\d+}")
    public Response updateComment(@PathParam("id") Long id, @Valid CommentDTO commentDTO) {
        // TODO: Verificar que el usuario logueado sea el dueño del comentario
        commentDTO.setId(id);
        CommentDTO responseDTO = commentService.update(commentDTO);
        CommentResource commentResource = commentResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildResponse(commentResource);
    }

    @DELETE
    @Path("/{id:\\d+}")
    public Response deleteComment(@PathParam("id") Long id) {
        // TODO: Verificar que el usuario logueado sea el dueño del comentario o moderador
        commentService.delete(id);
        return buildNoContentResponse();
    }
}

