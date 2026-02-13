package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.CommentDTO;
import ar.edu.itba.paw.webapp.form.CommentForm;
import ar.edu.itba.paw.webapp.mapper.dto.CommentDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.CommentFormMapper;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;
import ar.edu.itba.paw.webapp.utils.PaginationHeadersBuilder;
import ar.edu.itba.paw.webapp.utils.SecurityContextUtils;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
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
    private CommentFormMapper commentFormMapper;

    @Autowired
    private CommentDtoMapper commentDtoMapper;

    @GET
    @Produces(CustomMediaType.COMMENT_LIST)
    public Response getAllComments(
            @QueryParam(ControllerUtils.SEARCH_PARAM_NAME) String search,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {

        List<Comment> comments;
        if (search != null && !search.isEmpty()) {
            comments = commentService.findBySubstring(search, page, size);
        } else {
            comments = commentService.findPaginated(filter, page, size);
        }

        Long totalCount = commentService.countAll();

        if (comments.isEmpty()) {
            return Response.noContent().build();
        }

        List<CommentDTO> commentDTOs = commentDtoMapper.toDTOList(comments, uriInfo);

        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<CommentDTO>>(commentDTOs) {
                });

        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, page, size, totalCount);
        return responseBuilder.build();
    }

    @POST
    @Consumes(CustomMediaType.COMMENT)
    @Produces(CustomMediaType.COMMENT)
    public Response createComment(@Valid CommentForm commentForm) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        Comment comment = commentFormMapper.toModel(commentForm, loggedUserId);
        Comment createdComment = commentService.create(comment);
        CommentDTO commentDTO = commentDtoMapper.toDTO(createdComment, uriInfo);
        return Response.created(commentDTO.getLinks().getSelf()).entity(commentDTO).build();
    }

    @GET
    @Path(ApiUriConstants.ID)
    @Produces(CustomMediaType.COMMENT)
    public Response getComment(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        Comment comment = commentService.findById(id);
        CommentDTO commentDTO = commentDtoMapper.toDTO(comment, uriInfo);
        return Response.ok(commentDTO).build();
    }

    @DELETE
    @Path(ApiUriConstants.ID)
    @PreAuthorize("hasRole('MODERATOR')")
    public Response deleteComment(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        commentService.delete(id);
        return Response.noContent().build();
    }

    @PUT
    @Path(ApiUriConstants.ID)
    @Consumes(CustomMediaType.COMMENT)
    @Produces(CustomMediaType.COMMENT)
    public Response updateComment(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Valid CommentForm commentForm) {
        Comment comment = commentFormMapper.toModel(commentForm);
        comment.setId(id);
        Comment updatedComment = commentService.update(comment);
        CommentDTO commentDTO = commentDtoMapper.toDTO(updatedComment, uriInfo);
        return Response.ok(commentDTO).build();
    }
}
