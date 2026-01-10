package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.ReviewDTO;
import ar.edu.itba.paw.webapp.dto.SongDTO;
import ar.edu.itba.paw.webapp.mapper.dto.ReviewDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.SongDtoMapper;
import ar.edu.itba.paw.webapp.mapper.resource.CollectionResourceMapper;
import ar.edu.itba.paw.webapp.mapper.resource.ReviewResourceMapper;
import ar.edu.itba.paw.webapp.mapper.resource.SongResourceMapper;
import ar.edu.itba.paw.webapp.models.resources.CollectionResource;
import ar.edu.itba.paw.webapp.models.resources.ReviewResource;
import ar.edu.itba.paw.webapp.models.resources.SongResource;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import ar.edu.itba.paw.webapp.utils.SecurityContextUtils;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.services.SongService;
import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import ar.edu.itba.paw.webapp.form.ModSongForm;
import ar.edu.itba.paw.webapp.mapper.dto.ModSongFormMapper;
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
        CollectionResource<SongResource> collection = collectionResourceMapper.createCollection(
                songResources, songService.countAll().intValue(), page, size, getBaseUrl(), ApiUriConstants.SONGS_BASE, ControllerUtils.songsCollectionLinks);
        return buildPaginatedResponse(collection);
    }

    @POST
    @PreAuthorize("hasRole('MODERATOR')")
    public Response createSong(@Valid ModSongForm modSongForm) {
        Song songInput = modSongFormMapper.toModel(modSongForm);
        Song song = songService.create(songInput);
        SongDTO songDTO = songDtoMapper.toDTO(song);
        SongResource songResource = songResourceMapper.toResource(songDTO, getBaseUrl());
        return buildCreatedResponse(songResource, buildResourceLocation(ApiUriConstants.SONGS_BASE, song.getId()));
    }

    @GET
    @Path(ApiUriConstants.ID)
    public Response getSong(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Context Request request) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        Song song = songService.findAndSetContextDependentFields(id, loggedUserId);
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
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.USER_ID_PARAM_NAME) Long userId) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        List<Review> reviews = new ArrayList<>();
        if (userId != null) reviews.add(reviewService.findSongReviewByUserId(userId, id, loggedUserId));
        else reviews = reviewService.findSongReviewsPaginated(id, page, size, loggedUserId);
        List<ReviewDTO> reviewDTOs = reviewDtoMapper.toDTOList(reviews);
        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviewDTOs, getBaseUrl());
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                reviewResources, reviewService.countAll().intValue(), page, size, getBaseUrl(), ApiUriConstants.SONGS_BASE + ApiUriConstants.SONG_REVIEWS, ControllerUtils.itemReviewsCollectionLinks, id);
        return buildPaginatedResponse(collection);
    }

    @POST
    @Path(ApiUriConstants.SONG_FAVORITE)
    public Response addSongFavorite(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        userService.addFavoriteSong(loggedUserId, id);
        Song song = songService.findAndSetContextDependentFields(id, loggedUserId);
        SongDTO songDTO = songDtoMapper.toDTO(song);
        SongResource songResource = songResourceMapper.toResource(songDTO, getBaseUrl());

        return buildCreatedResponse(songResource,
                buildNestedResourceLocation(ApiUriConstants.USERS_BASE, loggedUserId, "/favorites/songs", id));
    }

    @DELETE
    @Path(ApiUriConstants.SONG_FAVORITE)
    public Response removeSongFavorite(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        userService.removeFavoriteSong(SecurityContextUtils.getCurrentUserId(), id);
        return buildNoContentResponse();
    }
}
