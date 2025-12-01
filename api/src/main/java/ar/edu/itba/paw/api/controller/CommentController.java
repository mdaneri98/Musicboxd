package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.dto.CommentDTO;
import ar.edu.itba.paw.api.form.CommentForm;
import ar.edu.itba.paw.api.mapper.dto.CommentDtoMapper;
import ar.edu.itba.paw.api.mapper.dto.CommentFormMapper;
import ar.edu.itba.paw.api.mapper.resource.CollectionResourceMapper;
import ar.edu.itba.paw.api.mapper.resource.CommentResourceMapper;
import ar.edu.itba.paw.api.models.resources.CollectionResource;
import ar.edu.itba.paw.api.models.resources.CommentResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.ControllerUtils;
import ar.edu.itba.paw.api.utils.SecurityContextUtils;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

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

    @Autowired
    private CommentFormMapper commentFormMapper;

    @Autowired
    private CommentDtoMapper commentDtoMapper;

    @GET
    public Response getAllComments(
            @QueryParam(ControllerUtils.SEARCH_PARAM_NAME) String search,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {
        
        if (search != null && !search.isEmpty()) return getCommentBySubstring(search, page, size);
        
        List<Comment> comments = commentService.findPaginated(filter, page, size);
        List<CommentDTO> commentDTOs = commentDtoMapper.toDTOList(comments);
        List<CommentResource> commentResources = commentResourceMapper.toResourceList(commentDTOs, getBaseUrl());
        Integer totalCount = commentService.countAll().intValue();
        
        CollectionResource<CommentResource> collection = collectionResourceMapper.createCollection(
                commentResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.COMMENTS_BASE, ControllerUtils.commentsCollectionLinks);
        
        return buildResponse(collection);
    }

    private Response getCommentBySubstring(String substring, Integer page, Integer size) {
        List<Comment> comments = commentService.findBySubstring(substring, page, size);
        List<CommentDTO> commentDTOs = commentDtoMapper.toDTOList(comments);
        List<CommentResource> commentResources = commentResourceMapper.toResourceList(commentDTOs, getBaseUrl());
        Integer totalCount = commentService.countAll().intValue();
        CollectionResource<CommentResource> collection = collectionResourceMapper.createCollection(
                commentResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.COMMENTS_BASE, ControllerUtils.commentsCollectionLinks);
        return buildResponse(collection);
    }

    @POST
    public Response createComment(@Valid CommentForm commentForm) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        Comment comment = commentFormMapper.toModel(commentForm, loggedUserId);
        Comment createdComment = commentService.create(comment);
        CommentDTO commentDTO = commentDtoMapper.toDTO(createdComment);
        CommentResource commentResource = commentResourceMapper.toResource(commentDTO, getBaseUrl());
        return buildCreatedResponse(commentResource);
    }

    @GET
    @Path(ApiUriConstants.ID)
    public Response getComment(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        Comment comment = commentService.findById(id);
        CommentDTO commentDTO = commentDtoMapper.toDTO(comment);
        CommentResource commentResource = commentResourceMapper.toResource(commentDTO, getBaseUrl());
        return buildResponse(commentResource);
    }

    @DELETE
    @Path(ApiUriConstants.ID)
    @PreAuthorize("hasRole('MODERATOR')")
    public Response deleteComment(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        commentService.delete(id);
        return buildNoContentResponse();
    }

    @PUT
    @Path(ApiUriConstants.ID)
    public Response updateComment(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Valid CommentForm commentForm) {
        Comment comment = commentFormMapper.toModel(commentForm);
        comment.setId(id);
        Comment updatedComment = commentService.update(comment);
        CommentDTO commentDTO = commentDtoMapper.toDTO(updatedComment);
        CommentResource commentResource = commentResourceMapper.toResource(commentDTO, getBaseUrl());
        return buildResponse(commentResource);
    }
}
