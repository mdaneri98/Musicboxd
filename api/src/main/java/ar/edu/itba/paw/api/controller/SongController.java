package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.mapper.CollectionResourceMapper;
import ar.edu.itba.paw.api.mapper.ReviewResourceMapper;
import ar.edu.itba.paw.api.mapper.SongResourceMapper;
import ar.edu.itba.paw.api.models.CollectionResource;
import ar.edu.itba.paw.api.models.ReviewResource;
import ar.edu.itba.paw.api.models.SongResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
import ar.edu.itba.paw.models.dtos.SongDTO;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.services.SongService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path(ApiUriConstants.SONGS_BASE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SongController extends BaseController {

    @Autowired
    private SongService songService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private SongResourceMapper songResourceMapper;

    @Autowired
    private ReviewResourceMapper reviewResourceMapper;

    @Autowired
    private CollectionResourceMapper collectionResourceMapper;

    @GET
    public Response getAllSongs(
            @QueryParam("search") String search,
            @QueryParam("artistId") Long artistId,
            @QueryParam("albumId") Long albumId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("filter") @DefaultValue("FIRST") FilterType filter) {
        
        List<SongDTO> songDTOs = songService.findPaginated(filter, page, size);
        List<SongResource> songResources = songResourceMapper.toResourceList(songDTOs, getBaseUrl());
        Long totalCount = songService.countAll();
        
        CollectionResource<SongResource> collection = collectionResourceMapper.createCollection(
                songResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.SONGS_BASE);
        
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.ID)
    public Response getSong(@PathParam("id") Long id) {
        SongDTO songDTO = songService.findById(id);
        SongResource songResource = songResourceMapper.toResource(songDTO, getBaseUrl());
        return buildResponse(songResource);
    }

    @POST
    public Response createSong(
            @Valid SongDTO songDTO) {
        
        if (songDTO.getAlbumId() == 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Album ID is required")
                    .build();
        }

        SongDTO responseDTO = songService.create(songDTO);
        SongResource songResource = songResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildCreatedResponse(songResource);
    }

    @PUT
    @Path(ApiUriConstants.ID)
    public Response updateSong(
            @PathParam("id") Long id,
            @Valid SongDTO songDTO,
            @QueryParam("albumId") Long albumId) {
        
        songDTO.setId(id);
        
        if (albumId != null) {
            songDTO.setAlbumId(albumId);
        }

        SongDTO responseDTO = songService.update(songDTO);
        SongResource songResource = songResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildResponse(songResource);
    }

    @DELETE
    @Path(ApiUriConstants.ID)
    public Response deleteSong(@PathParam("id") Long id) {
        songService.delete(id);
        return buildNoContentResponse();
    }

    @GET
    @Path(ApiUriConstants.SONG_REVIEWS)
    public Response getSongReviews(
            @PathParam("id") Long id,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("loggedUserId") @DefaultValue("1") Long loggedUserId) {
        // TODO: Obtener loggedUserId del contexto de seguridad
        
        List<ReviewDTO> reviews = reviewService.findSongReviewsPaginated(id, page, size, loggedUserId);

        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviews, getBaseUrl());
        Long totalCount = reviewService.countAll();
                
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                    reviewResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.SONG_REVIEWS);
        
        return buildResponse(collection);
    }

    @POST
    @Path(ApiUriConstants.SONG_REVIEWS)
    public Response createSongReview(
            @PathParam("id") Long id,
            @Valid ReviewDTO reviewDTO) {
        reviewDTO.setItemId(id);
        reviewDTO.setItemType("Song");
        ReviewDTO responseDTO = reviewService.create(reviewDTO);
        ReviewResource reviewResource = reviewResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildResponse(reviewResource);
    }

    @PUT
    @Path(ApiUriConstants.SONG_REVIEWS)
    public Response updateSongReview(
            @PathParam("id") Long id,
            @Valid ReviewDTO reviewDTO) {
        reviewDTO.setId(id);
        reviewDTO.setItemType("Song");
        ReviewDTO responseDTO = reviewService.update(reviewDTO);
        ReviewResource reviewResource = reviewResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildResponse(reviewResource);
    }
}

