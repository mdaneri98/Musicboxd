package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.mapper.CollectionResourceMapper;
import ar.edu.itba.paw.api.mapper.CommentResourceMapper;
import ar.edu.itba.paw.api.models.CollectionResource;
import ar.edu.itba.paw.api.models.CommentResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.dtos.CommentDTO;
import ar.edu.itba.paw.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path(ApiUriConstants.COMMENTS_BASE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommentController extends BaseController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentResourceMapper commentResourceMapper;

    @Autowired
    private CollectionResourceMapper collectionResourceMapper;

    @GET
    public Response getAllComments(
            @QueryParam("search") String search,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("filter") @DefaultValue("FIRST") FilterType filter) {
        
        List<CommentDTO> commentDTOs = commentService.findPaginated(filter, page, size);
        List<CommentResource> commentResources = commentResourceMapper.toResourceList(commentDTOs, getBaseUrl());
        Long totalCount = commentService.countAll();
        
        CollectionResource<CommentResource> collection = collectionResourceMapper.createCollection(
                commentResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.COMMENTS_BASE);
        
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.ID)
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

    @DELETE
    @Path(ApiUriConstants.ID)
    public Response deleteComment(@PathParam("id") Long id) {
        // TODO: Verificar que el usuario logueado sea el dueño del comentario o moderador
        commentService.delete(id);
        return buildNoContentResponse();
    }

    @PUT
    @Path(ApiUriConstants.ID)
    public Response updateComment(@PathParam("id") Long id, @Valid CommentDTO commentDTO) {
        commentDTO.setId(id);
        CommentDTO responseDTO = commentService.update(commentDTO);
        CommentResource commentResource = commentResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildResponse(commentResource);
    }
}
