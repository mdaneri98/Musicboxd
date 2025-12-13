package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.dto.ReviewDTO;
import ar.edu.itba.paw.api.dto.SongDTO;
import ar.edu.itba.paw.api.mapper.dto.ReviewDtoMapper;
import ar.edu.itba.paw.api.mapper.dto.SongDtoMapper;
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
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.services.SongService;
import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import ar.edu.itba.paw.api.form.ModSongForm;
import ar.edu.itba.paw.api.mapper.dto.ModSongFormMapper;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.ArrayList;

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

    @Autowired
    private UserService userService;

    @Autowired
    private ModSongFormMapper modSongFormMapper;

    @Autowired
    private SongDtoMapper songDtoMapper;

    @Autowired
    private ReviewDtoMapper reviewDtoMapper;

    @GET
    public Response getAllSongs(
            @QueryParam(ControllerUtils.SEARCH_PARAM_NAME) String search,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {
        List<Song> songs = new ArrayList<>();
        if (search != null && !search.isEmpty()) songs = songService.findByTitleContaining(search, page, size);
        else songs = songService.findPaginated(filter, page, size);
        List<SongDTO> songDTOs = songDtoMapper.toDTOList(songs);
        List<SongResource> songResources = songResourceMapper.toResourceList(songDTOs, getBaseUrl());
        Integer totalCount = songService.countAll().intValue();
        CollectionResource<SongResource> collection = collectionResourceMapper.createCollection(
                songResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.SONGS_BASE, ControllerUtils.songsCollectionLinks);
        return buildResponse(collection);
    }

    @POST
    @PreAuthorize("hasRole('MODERATOR')")
    public Response createSong(@Valid ModSongForm modSongForm) {
        Song songInput = modSongFormMapper.toModel(modSongForm);
        Song song = songService.create(songInput);
        SongDTO songDTO = songDtoMapper.toDTO(song);
        SongResource songResource = songResourceMapper.toResource(songDTO, getBaseUrl());
        return buildCreatedResponse(songResource);
    }

    @GET
    @Path(ApiUriConstants.ID)
    public Response getSong(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Context Request request) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        Song song = songService.findById(id);
        songService.setContextDependentFields(song, loggedUserId);
        return buildResponseUsingEtag(request, () -> {
            SongDTO songDTO = songDtoMapper.toDTO(song);
            return songResourceMapper.toResource(songDTO, getBaseUrl());
        });
    }

    @PUT
    @Path(ApiUriConstants.ID)
    @PreAuthorize("hasRole('MODERATOR')")
    public Response updateSong(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @Valid ModSongForm modSongForm) {
        Song oldSong = songService.findById(id);
        Song songToUpdate = modSongFormMapper.mergeModel(oldSong, modSongForm);
        Song updatedSong = songService.update(songToUpdate);
        SongDTO songDTO = songDtoMapper.toDTO(updatedSong);
        SongResource songResource = songResourceMapper.toResource(songDTO, getBaseUrl());
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
        List<Review> reviews = reviewService.findSongReviewsPaginated(id, page, size, loggedUserId);
        List<ReviewDTO> reviewDTOs = reviewDtoMapper.toDTOList(reviews, loggedUserId, reviewService);
        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviewDTOs, getBaseUrl());
        Integer totalCount = reviewService.countAll().intValue();    
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                reviewResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.SONGS_BASE + ApiUriConstants.SONG_REVIEWS, ControllerUtils.itemReviewsCollectionLinks, id);
        return buildResponse(collection);
    }

    @POST
    @Path(ApiUriConstants.SONG_FAVORITE)
    public Response addSongFavorite(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        userService.addFavoriteSong(loggedUserId, id);
        Song song = songService.findById(id);
        songService.setContextDependentFields(song, loggedUserId);
        SongDTO songDTO = songDtoMapper.toDTO(song);
        SongResource songResource = songResourceMapper.toResource(songDTO, getBaseUrl());
        return buildCreatedResponse(songResource);
    }

    @DELETE
    @Path(ApiUriConstants.SONG_FAVORITE)
    public Response removeSongFavorite(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        userService.removeFavoriteSong(SecurityContextUtils.getCurrentUserId(), id);
        return buildNoContentResponse();
    }
}
