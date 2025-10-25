package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.mapper.CollectionResourceMapper;
import ar.edu.itba.paw.api.mapper.CommentResourceMapper;
import ar.edu.itba.paw.api.mapper.ReviewResourceMapper;
import ar.edu.itba.paw.api.mapper.UserResourceMapper;
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
import org.springframework.beans.factory.annotation.Autowired;

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

    @GET
    public Response getAllReviews(
            @QueryParam("search") String search,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("filter") @DefaultValue("FIRST") FilterType filter) {

        if (search != null && !search.isEmpty()) return getReviewBySubstring(search, page, size);
        
        List<ReviewDTO> reviewDTOs = reviewService.findPaginated(filter, page, size);
        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviewDTOs, getBaseUrl());
        Long totalCount = reviewService.countAll();
        
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                reviewResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.REVIEWS_BASE);
        
        return buildResponse(collection);
    }

    private Response getReviewBySubstring(String substring, int page, int size) {
        List<ReviewDTO> reviewDTOs = reviewService.findBySubstring(substring, page, size);
        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviewDTOs, getBaseUrl());
        Long totalCount = reviewService.countAll();
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                reviewResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.REVIEWS_BASE);
        return buildResponse(collection);
    }

    @POST
    public Response createReview(@Valid ReviewDTO reviewDTO) {
        ReviewDTO responseDTO = reviewService.create(reviewDTO);
        ReviewResource reviewResource = reviewResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildResponse(reviewResource);
    }

    @GET
    @Path(ApiUriConstants.ID)
    public Response getReview(@PathParam("id") Long id) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        ReviewResource reviewResource = reviewResourceMapper.toResource(reviewService.findById(loggedUserId), getBaseUrl());
        return buildResponse(reviewResource);
    }

    @DELETE
    @Path(ApiUriConstants.ID)
    public Response deleteReview(@PathParam("id") Long id) {
        reviewService.delete(id);
        return buildNoContentResponse();
    }

    @PUT
    @Path(ApiUriConstants.ID)
    public Response updateReview(@PathParam("id") Long id, @Valid ReviewDTO reviewDTO) {
        reviewDTO.setId(id);
        ReviewDTO responseDTO = reviewService.update(reviewDTO);
        ReviewResource reviewResource = reviewResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildResponse(reviewResource);
    }

    @GET
    @Path(ApiUriConstants.REVIEW_COMMENTS)
    public Response getReviewComments(
            @PathParam("id") Long id,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        
        List<CommentDTO> commentDTOs = commentService.findByReviewId(id, size, page);
        List<CommentResource> commentResources = commentResourceMapper.toResourceList(commentDTOs, getBaseUrl());
        Long totalCount = commentService.countByReviewId(id);
        CollectionResource<CommentResource> collection = collectionResourceMapper.createCollection(
                commentResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.COMMENTS_BASE);
        
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.REVIEW_LIKES)
    public Response getReviewLikes(@PathParam("id") Long reviewId, @QueryParam("page") @DefaultValue("1") int page, @QueryParam("size") @DefaultValue("20") int size) {
        
        ReviewDTO reviewDTO = reviewService.findById(reviewId);
        List<UserDTO> userDTOs = reviewService.likedBy(reviewId, page, size);
        List<UserResource> userResources = userResourceMapper.toResourceList(userDTOs, getBaseUrl());
        Long totalCount = reviewDTO.getLikes().longValue();
        CollectionResource<UserResource> collection = collectionResourceMapper.createCollection(
                userResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.USERS_BASE);
        
        return buildResponse(collection);
    }

    @POST
    @Path(ApiUriConstants.REVIEW_LIKES)
        public Response likeReview(@PathParam("id") Long reviewId) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        
        reviewService.createLike(loggedUserId, reviewId);
        return Response.ok().entity("{\"message\": \"Review liked successfully\"}").build();
    }

    @DELETE
    @Path(ApiUriConstants.REVIEW_LIKES)
    public Response unlikeReview(@PathParam("id") Long reviewId) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        
        reviewService.removeLike(loggedUserId, reviewId);
        return buildNoContentResponse();
    }

    @POST
    @Path(ApiUriConstants.REVIEW_BLOCK)
    public Response blockReview(@PathParam("id") Long reviewId) {

        if (!SecurityContextUtils.isModerator()) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You are not allowed to block this review")
                    .build();
        }

        reviewService.block(reviewId);
        return Response.ok().entity("{\"message\": \"Review blocked successfully\"}").build();
    }

    @POST
    @Path(ApiUriConstants.REVIEW_UNBLOCK)
    public Response unblockReview(@PathParam("id") Long reviewId) {
        if (!SecurityContextUtils.isModerator()) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You are not allowed to unblock this review")
                    .build();
        }
        reviewService.unblock(reviewId);
        return Response.ok().entity("{\"message\": \"Review unblocked successfully\"}").build();
    }
}
