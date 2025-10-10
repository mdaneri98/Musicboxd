package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.mapper.CollectionResourceMapper;
import ar.edu.itba.paw.api.mapper.CommentResourceMapper;
import ar.edu.itba.paw.api.mapper.ReviewResourceMapper;
import ar.edu.itba.paw.api.models.CollectionResource;
import ar.edu.itba.paw.api.models.CommentResource;
import ar.edu.itba.paw.api.models.ReviewResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.models.dtos.CommentDTO;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.services.CommentService;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.models.FilterType;
import org.springframework.beans.factory.annotation.Autowired;

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
    private CollectionResourceMapper collectionResourceMapper;

    @GET
    public Response getAllReviews(
            @QueryParam("userId") Long userId,
            @QueryParam("popular") @DefaultValue("false") boolean popular,
            @QueryParam("following") @DefaultValue("false") boolean following,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("filter") @DefaultValue("FIRST") FilterType filter) {
        
        List<Review> reviewDTOs = reviewService.findPaginated(filter, page, size);
        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviewDTOs, getBaseUrl());
        
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                reviewResources, getBaseUrl(), ApiUriConstants.REVIEWS_BASE);
        
        return buildResponse(collection);
    }

    @GET
    @Path("/{id:\\d+}")
    public Response getReview(@PathParam("id") Long id, @QueryParam("loggedUserId") Long loggedUserId) {
        ReviewResource reviewResource;
        if (reviewService.isAlbumReview(id)) {
            AlbumReview review = reviewService.findAlbumReviewById(id, loggedUserId);
            reviewResource = reviewResourceMapper.toResource(review, getBaseUrl());
        } else if (reviewService.isSongReview(id)) {
            SongReview review = reviewService.findSongReviewById(id, loggedUserId);
            reviewResource = reviewResourceMapper.toResource(review, getBaseUrl());
        } else {
            ArtistReview review = reviewService.findArtistReviewById(id, loggedUserId);
            reviewResource = reviewResourceMapper.toResource(review, getBaseUrl());
        }
        return buildResponse(reviewResource);
    }

    @DELETE
    @Path("/{id:\\d+}")
    public Response deleteReview(@PathParam("id") Long id) {
        reviewService.delete(id);
        return buildNoContentResponse();
    }

    @GET
    @Path("/{id:\\d+}/comments")
    public Response getReviewComments(
            @PathParam("id") Long id,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        
        List<CommentDTO> commentDTOs = commentService.findByReviewId(id, page, size);
        List<CommentResource> commentResources = commentResourceMapper.toResourceList(commentDTOs, getBaseUrl());
        
        CollectionResource<CommentResource> collection = collectionResourceMapper.createCollection(
                commentResources, getBaseUrl(), ApiUriConstants.COMMENTS_BASE);
        
        return buildResponse(collection);
    }

    @POST
    @Path("/{id:\\d+}/likes")
    public Response likeReview(@PathParam("id") Long reviewId) {
        // TODO: Obtener userId del contexto de seguridad
        long userId = 1L;
        
        reviewService.createLike(userId, reviewId);
        return Response.ok().entity("{\"message\": \"Review liked successfully\"}").build();
    }

    @DELETE
    @Path("/{id:\\d+}/likes")
    public Response unlikeReview(@PathParam("id") Long reviewId) {
        // TODO: Obtener userId del contexto de seguridad
        long userId = 1L;
        
        reviewService.removeLike(userId, reviewId);
        return buildNoContentResponse();
    }

    @POST
    @Path("/{id:\\d+}/block")
    public Response blockReview(@PathParam("id") Long reviewId) {
        // TODO: Verificar que el usuario sea moderador
        reviewService.block(reviewId);
        return Response.ok().entity("{\"message\": \"Review blocked successfully\"}").build();
    }

    @POST
    @Path("/{id:\\d+}/unblock")
    public Response unblockReview(@PathParam("id") Long reviewId) {
        // TODO: Verificar que el usuario sea moderador
        reviewService.unblock(reviewId);
        return Response.ok().entity("{\"message\": \"Review unblocked successfully\"}").build();
    }
}

