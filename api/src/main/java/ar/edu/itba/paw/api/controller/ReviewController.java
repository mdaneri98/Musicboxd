package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.mapper.resource.CollectionResourceMapper;
import ar.edu.itba.paw.api.mapper.resource.CommentResourceMapper;
import ar.edu.itba.paw.api.mapper.resource.ReviewResourceMapper;
import ar.edu.itba.paw.api.mapper.resource.UserResourceMapper;
import ar.edu.itba.paw.api.models.resources.CollectionResource;
import ar.edu.itba.paw.api.models.resources.CommentResource;
import ar.edu.itba.paw.api.models.resources.ReviewResource;
import ar.edu.itba.paw.api.models.resources.UserResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.SecurityContextUtils;
import ar.edu.itba.paw.models.dtos.CommentDTO;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
import ar.edu.itba.paw.models.dtos.UserDTO;
import ar.edu.itba.paw.services.CommentService;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.api.form.ReviewForm;
import ar.edu.itba.paw.api.form.CommentForm;
import ar.edu.itba.paw.api.mapper.dto.ReviewFormMapper;
import ar.edu.itba.paw.api.mapper.dto.CommentFormMapper;
import org.springframework.beans.factory.annotation.Autowired;
import ar.edu.itba.paw.api.utils.ControllerUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path(ApiUriConstants.REVIEWS_BASE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReviewController extends BaseController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ReviewResourceMapper reviewResourceMapper;

    @Autowired
    private CommentResourceMapper commentResourceMapper;

    @Autowired
    private UserResourceMapper userResourceMapper;

    @Autowired
    private CollectionResourceMapper collectionResourceMapper;

    @Autowired
    private ReviewFormMapper reviewFormMapper;

    @Autowired
    private CommentFormMapper commentFormMapper;

    @GET
    public Response getAllReviews(
            @QueryParam(ControllerUtils.SEARCH_PARAM_NAME) String search,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {

        List<ReviewDTO> reviewDTOs;
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        if (search != null && !search.isEmpty()) return getReviewBySubstring(search, page, size);
        if (filter == FilterType.FOLLOWING) return getReviewsFromFollowedUsersPaginated(page, size, loggedUserId);
        reviewDTOs = reviewService.findPaginated(filter, page, size, loggedUserId);

        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviewDTOs, getBaseUrl());
        Integer totalCount = reviewService.countAll().intValue();
        
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                reviewResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.REVIEWS_BASE, ControllerUtils.reviewsCollectionLinks);
        
        return buildResponse(collection);
    }

    private Response getReviewsFromFollowedUsersPaginated(Integer page, Integer size, Long loggedUserId) {
        List<ReviewDTO> reviewDTOs = reviewService.getReviewsFromFollowedUsersPaginated(page, size, loggedUserId);
        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviewDTOs, getBaseUrl());
        Integer totalCount = reviewService.countReviewsFromFollowedUsers(loggedUserId).intValue();
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                reviewResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.REVIEWS_BASE, ControllerUtils.reviewsCollectionLinks);
        return buildResponse(collection);
    }

    private Response getReviewBySubstring(String substring, Integer page, Integer size) {
        List<ReviewDTO> reviewDTOs = reviewService.findBySubstring(substring, page, size);
        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviewDTOs, getBaseUrl());
        Integer totalCount = reviewService.countAll().intValue();
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                reviewResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.REVIEWS_BASE, ControllerUtils.reviewsCollectionLinks);
        return buildResponse(collection);
    }

    @POST
    public Response createReview(@Valid ReviewForm reviewForm) {
        ReviewDTO reviewDTO = reviewFormMapper.toDTO(reviewForm);
        ReviewDTO responseDTO = reviewService.create(reviewDTO);
        ReviewResource reviewResource = reviewResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildResponse(reviewResource);
    }

    @GET
    @Path(ApiUriConstants.ID)
    public Response getReview(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        ReviewResource reviewResource = reviewResourceMapper.toResource(reviewService.findById(id), getBaseUrl());
        return buildResponse(reviewResource);
    }

    @DELETE
    @Path(ApiUriConstants.ID)
    @PreAuthorize("hasRole('MODERATOR')")
    public Response deleteReview(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        reviewService.delete(id);
        return buildNoContentResponse();
    }

    @PUT
    @Path(ApiUriConstants.ID)
    public Response updateReview(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Valid ReviewForm reviewForm) {
        ReviewDTO reviewDTO = reviewFormMapper.toDTO(reviewForm);
        reviewDTO.setId(id);
        ReviewDTO responseDTO = reviewService.update(reviewDTO);
        ReviewResource reviewResource = reviewResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildResponse(reviewResource);
    }

    @GET
    @Path(ApiUriConstants.REVIEW_COMMENTS)
    public Response getReviewComments(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size) {
        
        List<CommentDTO> commentDTOs = commentService.findByReviewId(id, size, page);
        List<CommentResource> commentResources = commentResourceMapper.toResourceList(commentDTOs, getBaseUrl());
        Integer totalCount = commentService.countByReviewId(id).intValue();
        CollectionResource<CommentResource> collection = collectionResourceMapper.createCollection(
                commentResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.COMMENTS_BASE, ControllerUtils.commentsCollectionLinks, id);
        
        return buildResponse(collection);
    }

    @POST
    @Path(ApiUriConstants.REVIEW_COMMENTS)
    public Response createReviewComment(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Valid CommentForm commentForm) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();

        CommentDTO commentDTO = commentFormMapper.toDTO(commentForm);
        commentDTO.setReviewId(id);
        commentDTO.setUserId(loggedUserId);
        CommentDTO responseDTO = commentService.create(commentDTO);

        CommentResource commentResource = commentResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildCreatedResponse(commentResource);
    }

    @GET
    @Path(ApiUriConstants.REVIEW_LIKES)
    public Response getReviewLikes(@PathParam(ControllerUtils.ID_PARAM_NAME) Long reviewId, 
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page, 
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size) {
        ReviewDTO reviewDTO = reviewService.findById(reviewId);
        List<UserDTO> userDTOs = reviewService.likedBy(reviewId, page, size);
        List<UserResource> userResources = userResourceMapper.toResourceList(userDTOs, getBaseUrl());
        Integer totalCount = reviewDTO.getLikes().intValue();
        CollectionResource<UserResource> collection = collectionResourceMapper.createCollection(
                userResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.REVIEWS_BASE + ApiUriConstants.REVIEW_LIKES, ControllerUtils.likesCollectionLinks, reviewId);
        
        return buildResponse(collection);
    }

    @POST
    @Path(ApiUriConstants.REVIEW_LIKES)
        public Response likeReview(@PathParam(ControllerUtils.ID_PARAM_NAME) Long reviewId) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        
        reviewService.createLike(loggedUserId, reviewId);
        return buildCreatedResponse(null);
    }

    @DELETE
    @Path(ApiUriConstants.REVIEW_LIKES)
    public Response unlikeReview(@PathParam(ControllerUtils.ID_PARAM_NAME) Long reviewId) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        
        reviewService.removeLike(loggedUserId, reviewId);
        return buildNoContentResponse();
    }


    @PATCH
    @Path(ApiUriConstants.ID)
    @PreAuthorize("hasRole('MODERATOR')")
    public Response updateBlockReviewStatus(@PathParam(ControllerUtils.ID_PARAM_NAME) Long reviewId, Boolean isBlocked) {
        if (isBlocked) reviewService.block(reviewId);
        else reviewService.unblock(reviewId);
        
        return buildNoContentResponse();
    }
}
