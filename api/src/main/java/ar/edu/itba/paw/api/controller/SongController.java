package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.mapper.resource.CollectionResourceMapper;
import ar.edu.itba.paw.api.mapper.resource.ReviewResourceMapper;
import ar.edu.itba.paw.api.mapper.resource.SongResourceMapper;
import ar.edu.itba.paw.api.models.resources.CollectionResource;
import ar.edu.itba.paw.api.models.resources.ReviewResource;
import ar.edu.itba.paw.api.models.resources.SongResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.ControllerUtils;
import ar.edu.itba.paw.api.utils.SecurityContextUtils;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
import ar.edu.itba.paw.models.dtos.SongDTO;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.services.SongService;
import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import ar.edu.itba.paw.api.mapper.dto.ReviewFormMapper;
import ar.edu.itba.paw.api.form.ReviewForm;
import ar.edu.itba.paw.api.form.ModSongForm;
import ar.edu.itba.paw.api.mapper.dto.ModSongFormMapper;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

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
    private ReviewFormMapper reviewFormMapper;

    @Autowired
    private CollectionResourceMapper collectionResourceMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ModSongFormMapper modSongFormMapper;

    @GET
    public Response getAllSongs(
            @QueryParam(ControllerUtils.SEARCH_PARAM_NAME) String search,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {

        if (search != null && !search.isEmpty()) return getSongBySubstring(search, page, size);

        List<SongDTO> songDTOs = songService.findPaginated(filter, page, size);
        List<SongResource> songResources = songResourceMapper.toResourceList(songDTOs, getBaseUrl());
        Integer totalCount = songService.countAll().intValue();
        
        CollectionResource<SongResource> collection = collectionResourceMapper.createCollection(
                songResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.SONGS_BASE, ControllerUtils.songsCollectionLinks);
        
        return buildResponse(collection);
    }

    private Response getSongBySubstring(String substring, Integer page, Integer size) {
        List<SongDTO> songDTOs = songService.findByTitleContaining(substring, page, size);
        List<SongResource> songResources = songResourceMapper.toResourceList(songDTOs, getBaseUrl());
        Integer totalCount = songService.countAll().intValue();
        CollectionResource<SongResource> collection = collectionResourceMapper.createCollection(
                songResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.SONGS_BASE, ControllerUtils.songsCollectionLinks);
        return buildResponse(collection);
    }

    @POST
    @PreAuthorize("hasRole('MODERATOR')")
    public Response createSong(
            @Valid ModSongForm modSongForm) {
        SongDTO songDTO = modSongFormMapper.toDTO(modSongForm);
        SongDTO responseDTO = songService.create(songDTO);
        SongResource songResource = songResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildCreatedResponse(songResource);
    }

    @GET
    @Path(ApiUriConstants.ID)
    public Response getSong(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        SongDTO songDTO = songService.findById(id, SecurityContextUtils.getCurrentUserId());
        SongResource songResource = songResourceMapper.toResource(songDTO, getBaseUrl());
        return buildResponse(songResource);
    }

    @PUT
    @Path(ApiUriConstants.ID)
    @PreAuthorize("hasRole('MODERATOR')")
    public Response updateSong(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @Valid ModSongForm modSongForm) {
        SongDTO songDTO = modSongFormMapper.toDTO(modSongForm);
        songDTO.setId(id);
        SongDTO responseDTO = songService.update(songDTO);
        SongResource songResource = songResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildResponse(songResource);
    }

    @DELETE
    @Path(ApiUriConstants.ID)
    @PreAuthorize("hasRole('MODERATOR')")
    public Response deleteSong(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        songService.delete(id);
        return buildNoContentResponse();
    }

    @GET
    @Path(ApiUriConstants.SONG_REVIEWS)
    public Response getSongReviews(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size) {

        Long loggedUserId = SecurityContextUtils.getCurrentUserId();

        List<ReviewDTO> reviews = reviewService.findSongReviewsPaginated(id, page, size, loggedUserId);

        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviews, getBaseUrl());
        Integer totalCount = reviewService.countAll().intValue();
                
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                    reviewResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.SONGS_BASE + ApiUriConstants.SONG_REVIEWS, ControllerUtils.itemReviewsCollectionLinks, id);
        
        return buildResponse(collection);
    }

    @POST
    @Path(ApiUriConstants.SONG_REVIEWS)
    public Response createSongReview(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @Valid ReviewForm reviewForm) {
        ReviewDTO reviewDTO = reviewFormMapper.toDTO(reviewForm);
        reviewDTO.setItemId(id);
        reviewDTO.setItemType(ControllerUtils.ITEM_TYPE_SONG);
        ReviewDTO responseDTO = reviewService.create(reviewDTO);
        ReviewResource reviewResource = reviewResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildResponse(reviewResource);
    }

    @POST
    @Path(ApiUriConstants.SONG_FAVORITE)
    public Response addSongFavorite(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        userService.addFavoriteSong(SecurityContextUtils.getCurrentUserId(), id);
        return buildCreatedResponse(songResourceMapper.toResource(songService.findById(id, SecurityContextUtils.getCurrentUserId()), getBaseUrl()));
    }

    @DELETE
    @Path(ApiUriConstants.SONG_FAVORITE)
    public Response removeSongFavorite(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        userService.removeFavoriteSong(SecurityContextUtils.getCurrentUserId(), id);
        return buildNoContentResponse();
    }
}

