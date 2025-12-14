package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.dto.CommentDTO;
import ar.edu.itba.paw.api.dto.ReviewDTO;
import ar.edu.itba.paw.api.dto.UserDTO;
import ar.edu.itba.paw.api.form.ReviewForm;
import ar.edu.itba.paw.api.mapper.dto.CommentDtoMapper;
import ar.edu.itba.paw.api.mapper.dto.ReviewDtoMapper;
import ar.edu.itba.paw.api.mapper.dto.ReviewFormMapper;
import ar.edu.itba.paw.api.mapper.dto.UserDtoMapper;
import ar.edu.itba.paw.api.mapper.resource.CollectionResourceMapper;
import ar.edu.itba.paw.api.mapper.resource.CommentResourceMapper;
import ar.edu.itba.paw.api.mapper.resource.ReviewResourceMapper;
import ar.edu.itba.paw.api.mapper.resource.UserResourceMapper;
import ar.edu.itba.paw.api.models.resources.CollectionResource;
import ar.edu.itba.paw.api.models.resources.CommentResource;
import ar.edu.itba.paw.api.models.resources.ReviewResource;
import ar.edu.itba.paw.api.models.resources.UserResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.ControllerUtils;
import ar.edu.itba.paw.api.utils.SecurityContextUtils;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.services.CommentService;
import ar.edu.itba.paw.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
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
    private ReviewDtoMapper reviewDtoMapper;

    @Autowired
    private UserDtoMapper userDtoMapper;

    @Autowired
    private CommentDtoMapper commentDtoMapper;

    @GET
    public Response getAllReviews(
            @QueryParam(ControllerUtils.SEARCH_PARAM_NAME) String search,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();

        List<Review> reviews;
        if (search != null && !search.isEmpty())
            reviews = reviewService.findBySubstring(search, page, size);
        else
            reviews = reviewService.findPaginated(filter, page, size, loggedUserId);

        List<ReviewDTO> reviewDTOs = reviewDtoMapper.toDTOList(reviews);
        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviewDTOs, getBaseUrl());
        Integer totalCount = reviewService.countAll().intValue();
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                reviewResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.REVIEWS_BASE, ControllerUtils.reviewsCollectionLinks);
        return buildResponse(collection);
    }

    @POST
    public Response createReview(@Valid ReviewForm reviewForm) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        Review reviewInput = reviewFormMapper.toModel(reviewForm, loggedUserId, reviewForm.getItemId().longValue());
        Review review = reviewService.create(reviewInput);
        ReviewDTO reviewDTO = reviewDtoMapper.toDTO(review);
        ReviewResource reviewResource = reviewResourceMapper.toResource(reviewDTO, getBaseUrl());
        return buildResponse(reviewResource);
    }

    @GET
    @Path(ApiUriConstants.ID)
    public Response getReview(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Context Request request) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        Review review = reviewService.findById(id);
        reviewService.setContextDependentFields(review, loggedUserId);
        return buildResponseUsingEtag(request, () -> {
            ReviewDTO reviewDTO = reviewDtoMapper.toDTO(review);
            return reviewResourceMapper.toResource(reviewDTO, getBaseUrl());
        });
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
        Review reviewToUpdate = reviewFormMapper.toModel(id, reviewForm);
        Review updatedReview = reviewService.update(reviewToUpdate);
        ReviewResource reviewResource = reviewResourceMapper.toResource(reviewDtoMapper.toDTO(updatedReview), getBaseUrl());
        return buildResponse(reviewResource);
    }

    @GET
    @Path(ApiUriConstants.REVIEW_COMMENTS)
    public Response getReviewComments(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size) {
        List<Comment> comments = commentService.findByReviewId(id, size, page);
        List<CommentDTO> commentDTOs = commentDtoMapper.toDTOList(comments);
        List<CommentResource> commentResources = commentResourceMapper.toResourceList(commentDTOs, getBaseUrl());
        Integer totalCount = commentService.countByReviewId(id).intValue();
        CollectionResource<CommentResource> collection = collectionResourceMapper.createCollection(
                commentResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.COMMENTS_BASE, ControllerUtils.commentsCollectionLinks, id);
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.REVIEW_LIKES)
    public Response getReviewLikes(@PathParam(ControllerUtils.ID_PARAM_NAME) Long reviewId, 
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page, 
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size) {  
        Review review = reviewService.findById(reviewId);
        List<User> users = reviewService.likedBy(reviewId, page, size);
        List<UserDTO> userDTOs = userDtoMapper.toDTOList(users);
        List<UserResource> userResources = userResourceMapper.toResourceList(userDTOs, getBaseUrl());
        Integer totalCount = review.getLikes();
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
}
