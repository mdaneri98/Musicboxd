package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.CommentDTO;
import ar.edu.itba.paw.webapp.dto.ReviewDTO;
import ar.edu.itba.paw.webapp.dto.UserDTO;
import ar.edu.itba.paw.webapp.form.ReviewForm;
import ar.edu.itba.paw.webapp.mapper.dto.CommentDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.ReviewDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.ReviewFormMapper;
import ar.edu.itba.paw.webapp.mapper.dto.UserDtoMapper;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;
import ar.edu.itba.paw.webapp.utils.PaginationHeadersBuilder;
import ar.edu.itba.paw.webapp.utils.SecurityContextUtils;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.domain.user.User;
import ar.edu.itba.paw.domain.review.Review;
import ar.edu.itba.paw.domain.review.ReviewType;
import ar.edu.itba.paw.services.CommentService;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.usecases.review.CreateReviewCommand;
import ar.edu.itba.paw.usecases.review.ReviewApplicationService;
import ar.edu.itba.paw.usecases.review.UpdateReviewCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.net.URI;
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
    private ReviewApplicationService reviewApplicationService;

    @Autowired
    private ReviewFormMapper reviewFormMapper;

    @Autowired
    private ReviewDtoMapper reviewDtoMapper;

    @Autowired
    private UserDtoMapper userDtoMapper;

    @Autowired
    private CommentDtoMapper commentDtoMapper;

    @GET
    @Produces(CustomMediaType.REVIEW_LIST)
    public Response getAllReviews(
            @QueryParam(ControllerUtils.SEARCH_PARAM_NAME) String search,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {

        List<Review> reviews;
        if (search != null && !search.isEmpty()) {
            List<ar.edu.itba.paw.models.reviews.Review> legacyReviews = reviewService.findBySubstring(search, page, size);
            Long totalCount = reviewService.countAll();
            if (legacyReviews.isEmpty()) {
                return Response.noContent().build();
            }
            List<ReviewDTO> reviewDTOs = reviewDtoMapper.toDTOListLegacy(legacyReviews, uriInfo);
            Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<ReviewDTO>>(reviewDTOs) {});
            PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, page, size, totalCount);
            return responseBuilder.build();
        } else {
            reviews = reviewApplicationService.getAllReviews(page, size);
        }

        Long totalCount = reviewApplicationService.countAllReviews();

        if (reviews.isEmpty()) {
            return Response.noContent().build();
        }

        List<ReviewDTO> reviewDTOs = reviewDtoMapper.toDTOList(reviews, uriInfo);

        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<ReviewDTO>>(reviewDTOs) {
                });

        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, page, size, totalCount);
        return responseBuilder.build();
    }

    @POST
    @Consumes(CustomMediaType.REVIEW)
    @Produces(CustomMediaType.REVIEW)
    public Response createReview(@Valid ReviewForm reviewForm) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        ReviewType reviewType = ReviewType.valueOf(reviewForm.getItemType());
        CreateReviewCommand command = new CreateReviewCommand(
            loggedUserId,
            reviewForm.getTitle(),
            reviewForm.getDescription(),
            reviewForm.getRating(),
            reviewType,
            reviewForm.getItemId().longValue()
        );
        Review review = reviewApplicationService.createReview(command);
        ReviewDTO reviewDTO = reviewDtoMapper.toDTO(review, uriInfo);
        return Response.created(reviewDTO.getLinks().getSelf()).entity(reviewDTO).build();
    }

    @GET
    @Path(ApiUriConstants.ID)
    @Produces(CustomMediaType.REVIEW)
    public Response getReview(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Context Request request) {
        Review review = reviewApplicationService.getReviewById(id);
        return buildResponseUsingEtag(request, () -> reviewDtoMapper.toDTO(review, uriInfo));
    }

    @DELETE
    @Path(ApiUriConstants.ID)
    @PreAuthorize("@securityServiceImpl.isReviewOwner(#id, authentication) or hasRole('MODERATOR')")
    public Response deleteReview(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        reviewApplicationService.deleteReview(id);
        return Response.noContent().build();
    }

    @PUT
    @Path(ApiUriConstants.ID)
    @PreAuthorize("@securityServiceImpl.isReviewOwner(#id, authentication)")
    @Consumes(CustomMediaType.REVIEW)
    @Produces(CustomMediaType.REVIEW)
    public Response updateReview(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Valid ReviewForm reviewForm) {
        UpdateReviewCommand command = new UpdateReviewCommand(
            id,
            reviewForm.getTitle(),
            reviewForm.getDescription(),
            reviewForm.getRating()
        );
        Review updatedReview = reviewApplicationService.updateReview(command);
        ReviewDTO reviewDTO = reviewDtoMapper.toDTO(updatedReview, uriInfo);
        return Response.ok(reviewDTO).build();
    }

    @GET
    @Path(ApiUriConstants.REVIEW_COMMENTS)
    @Produces(CustomMediaType.COMMENT_LIST)
    public Response getReviewComments(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size) {

        List<Comment> comments = commentService.findByReviewId(id, size, page);
        Long totalCount = commentService.countByReviewId(id);

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

    @GET
    @Path(ApiUriConstants.REVIEW_LIKES)
    @Produces(CustomMediaType.USER_LIST)
    public Response getReviewLikes(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long reviewId,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size) {

        Review review = reviewApplicationService.getReviewById(reviewId);
        List<User> users = reviewService.likedBy(reviewId, page, size);
        Integer totalCount = review.getLikes();

        if (users.isEmpty()) {
            return Response.noContent().build();
        }

        List<UserDTO> userDTOs = userDtoMapper.toDTOList(users, uriInfo);

        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<UserDTO>>(userDTOs) {
                });

        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, page, size, totalCount.longValue());
        return responseBuilder.build();
    }

    @GET
    @Path(ApiUriConstants.REVIEW_LIKE_DETAIL)
    public Response getReviewLikeByUser(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long reviewId,
            @PathParam(ControllerUtils.USER_ID_PARAM_NAME) Long userId) {
        if (reviewApplicationService.isReviewLiked(reviewId, userId)) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path(ApiUriConstants.REVIEW_LIKES)
    @PreAuthorize("hasRole('USER')")
    public Response likeReview(@PathParam(ControllerUtils.ID_PARAM_NAME) Long reviewId) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        reviewApplicationService.likeReview(reviewId, loggedUserId);

        URI location = uriInfo.getBaseUriBuilder()
                .path("reviews").path(String.valueOf(reviewId))
                .path("likes").path(String.valueOf(loggedUserId))
                .build();
        return Response.created(location).build();
    }

    @DELETE
    @Path(ApiUriConstants.REVIEW_LIKE_DETAIL)
    @PreAuthorize("@securityServiceImpl.isCurrentUser(#userId, authentication)")
    public Response unlikeReview(@PathParam(ControllerUtils.ID_PARAM_NAME) Long reviewId,
                                 @PathParam(ControllerUtils.USER_ID_PARAM_NAME) Long userId) {
        reviewApplicationService.unlikeReview(reviewId, userId);
        return Response.noContent().build();
    }
}
