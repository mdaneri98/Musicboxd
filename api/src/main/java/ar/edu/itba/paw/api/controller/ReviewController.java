package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.mapper.CollectionResourceMapper;
import ar.edu.itba.paw.api.mapper.CommentResourceMapper;
import ar.edu.itba.paw.api.mapper.ReviewResourceMapper;
import ar.edu.itba.paw.api.models.CollectionResource;
import ar.edu.itba.paw.api.models.CommentResource;
import ar.edu.itba.paw.api.models.ReviewResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.models.dtos.CommentDTO;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
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
    private CollectionResourceMapper collectionResourceMapper;

    @GET
    public Response getAllReviews(
            @QueryParam("popular") @DefaultValue("false") boolean popular,
            @QueryParam("following") @DefaultValue("false") boolean following,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("filter") @DefaultValue("FIRST") FilterType filter) {

        List<ReviewDTO> reviewDTOs = reviewService.findPaginated(filter, page, size);
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
    public Response getReview(@PathParam("id") Long id, @QueryParam("loggedUserId") @DefaultValue("23") Long loggedUserId) {
        // TODO: Obtener loggedUserId del contexto de seguridad
        ReviewResource reviewResource = reviewResourceMapper.toResource(reviewService.findById(id), getBaseUrl());
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
        
        List<CommentDTO> commentDTOs = commentService.findByReviewId(id, page, size);
        List<CommentResource> commentResources = commentResourceMapper.toResourceList(commentDTOs, getBaseUrl());
        Long totalCount = commentService.countByReviewId(id);
        CollectionResource<CommentResource> collection = collectionResourceMapper.createCollection(
                commentResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.COMMENTS_BASE);
        
        return buildResponse(collection);
    }

    @POST
    @Path(ApiUriConstants.REVIEW_LIKES)
    public Response likeReview(@PathParam("id") Long reviewId) {
        // TODO: Obtener userId del contexto de seguridad
        long userId = 1L;
        
        reviewService.createLike(userId, reviewId);
        return Response.ok().entity("{\"message\": \"Review liked successfully\"}").build();
    }

    @DELETE
    @Path(ApiUriConstants.REVIEW_LIKES)
    public Response unlikeReview(@PathParam("id") Long reviewId) {
        // TODO: Obtener userId del contexto de seguridad
        long userId = 1L;
        
        reviewService.removeLike(userId, reviewId);
        return buildNoContentResponse();
    }

    @POST
    @Path(ApiUriConstants.REVIEW_BLOCK)
    public Response blockReview(@PathParam("id") Long reviewId) {
        // TODO: Verificar que el usuario sea moderador
        reviewService.block(reviewId);
        return Response.ok().entity("{\"message\": \"Review blocked successfully\"}").build();
    }

    @POST
    @Path(ApiUriConstants.REVIEW_UNBLOCK)
    public Response unblockReview(@PathParam("id") Long reviewId) {
        // TODO: Verificar que el usuario sea moderador
        reviewService.unblock(reviewId);
        return Response.ok().entity("{\"message\": \"Review unblocked successfully\"}").build();
    }
}
