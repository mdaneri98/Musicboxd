package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.mapper.CollectionResourceMapper;
import ar.edu.itba.paw.api.mapper.ReviewResourceMapper;
import ar.edu.itba.paw.api.models.links.managers.CollectionLinkManager;
import ar.edu.itba.paw.api.mapper.SongResourceMapper;
import ar.edu.itba.paw.api.models.resources.CollectionResource;
import ar.edu.itba.paw.api.models.resources.ReviewResource;
import ar.edu.itba.paw.api.models.resources.SongResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.SecurityContextUtils;
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

    private CollectionLinkManager songsCollectionLinks = new CollectionLinkManager(true, false, false, true, true);
    private CollectionLinkManager reviewsCollectionLinks = new CollectionLinkManager(true, false, false, false, true);

    @GET
    public Response getAllSongs(
            @QueryParam("search") String search,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("filter") @DefaultValue("FIRST") FilterType filter) {

        if (search != null && !search.isEmpty()) return getSongBySubstring(search, page, size);

        List<SongDTO> songDTOs = songService.findPaginated(filter, page, size);
        List<SongResource> songResources = songResourceMapper.toResourceList(songDTOs, getBaseUrl());
        Long totalCount = songService.countAll();
        
        CollectionResource<SongResource> collection = collectionResourceMapper.createCollection(
                songResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.SONGS_BASE, songsCollectionLinks, null);
        
        return buildResponse(collection);
    }

    private Response getSongBySubstring(String substring, int page, int size) {
        List<SongDTO> songDTOs = songService.findByTitleContaining(substring, page, size);
        List<SongResource> songResources = songResourceMapper.toResourceList(songDTOs, getBaseUrl());
        Long totalCount = songService.countAll();
        CollectionResource<SongResource> collection = collectionResourceMapper.createCollection(
                songResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.SONGS_BASE, songsCollectionLinks, null);
        return buildResponse(collection);
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

    @GET
    @Path(ApiUriConstants.ID)
    public Response getSong(@PathParam("id") Long id) {
        SongDTO songDTO = songService.findById(id);
        SongResource songResource = songResourceMapper.toResource(songDTO, getBaseUrl());
        return buildResponse(songResource);
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
            @QueryParam("size") @DefaultValue("20") int size) {

        Long loggedUserId = SecurityContextUtils.getCurrentUserId();

        List<ReviewDTO> reviews = reviewService.findSongReviewsPaginated(id, page, size, loggedUserId);

        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviews, getBaseUrl());
        Long totalCount = reviewService.countAll();
                
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                    reviewResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.SONGS_BASE + ApiUriConstants.SONG_REVIEWS, reviewsCollectionLinks, id);
        
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
}

