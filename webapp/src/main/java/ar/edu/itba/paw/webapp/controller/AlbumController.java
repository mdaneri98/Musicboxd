package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.AlbumDTO;
import ar.edu.itba.paw.webapp.dto.ReviewDTO;
import ar.edu.itba.paw.webapp.dto.SongDTO;
import ar.edu.itba.paw.webapp.mapper.dto.AlbumDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.SongDtoMapper;
import ar.edu.itba.paw.webapp.mapper.resource.AlbumResourceMapper;
import ar.edu.itba.paw.webapp.mapper.resource.CollectionResourceMapper;
import ar.edu.itba.paw.webapp.mapper.resource.ReviewResourceMapper;
import ar.edu.itba.paw.webapp.mapper.resource.SongResourceMapper;
import ar.edu.itba.paw.webapp.models.resources.AlbumResource;
import ar.edu.itba.paw.webapp.models.resources.CollectionResource;
import ar.edu.itba.paw.webapp.models.resources.ReviewResource;
import ar.edu.itba.paw.webapp.models.resources.SongResource;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.webapp.utils.SecurityContextUtils;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.services.AlbumService;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.services.SongService;
import ar.edu.itba.paw.webapp.form.ModAlbumForm;
import ar.edu.itba.paw.webapp.form.ModSongForm;
import ar.edu.itba.paw.webapp.mapper.dto.ModAlbumFormMapper;
import ar.edu.itba.paw.webapp.mapper.dto.ReviewDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.ModSongFormMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.ArrayList;

@Path(ApiUriConstants.ALBUMS_BASE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AlbumController extends BaseController {

    @Autowired
    private AlbumService albumService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private SongService songService;

    @Autowired
    private UserService userService;

    @Autowired
    private AlbumResourceMapper albumResourceMapper;

    @Autowired
    private ReviewResourceMapper reviewResourceMapper;

    @Autowired
    private SongResourceMapper songResourceMapper;

    @Autowired
    private CollectionResourceMapper collectionResourceMapper;

    @Autowired
    private ModAlbumFormMapper modAlbumFormMapper;

    @Autowired
    private ModSongFormMapper modSongFormMapper;

    @Autowired
    private AlbumDtoMapper albumDtoMapper;

    @Autowired
    private SongDtoMapper songDtoMapper;

    @Autowired
    private ReviewDtoMapper reviewDtoMapper;


    @GET
    public Response getAllAlbums(
            @QueryParam(ControllerUtils.SEARCH_PARAM_NAME) String search,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {

        List<Album> albums = new ArrayList<>();
        if (search != null && !search.isEmpty()) albums = albumService.findByTitleContaining(search, page, size);
        else albums = albumService.findPaginated(filter, page, size);
        List<AlbumDTO> albumDTOs = albumDtoMapper.toDTOList(albums);
        List<AlbumResource> albumResources = albumResourceMapper.toResourceList(albumDTOs, getBaseUrl());
        CollectionResource<AlbumResource> collection = collectionResourceMapper.createCollection(
                albumResources, albumService.countAll().intValue(), page, size, getBaseUrl(), ApiUriConstants.ALBUMS_BASE, ControllerUtils.albumsCollectionLinks);
        return buildPaginatedResponse(collection);
    }

    @POST
    @PreAuthorize("hasRole('MODERATOR')")
    public Response createAlbum(@Valid ModAlbumForm modAlbumForm) {
        Album albumInput = modAlbumFormMapper.toModel(modAlbumForm);
        Album album = albumService.create(albumInput);
        AlbumDTO albumDTO = albumDtoMapper.toDTO(album);
        AlbumResource albumResource = albumResourceMapper.toResource(albumDTO, getBaseUrl());
        return buildCreatedResponse(albumResource, buildResourceLocation(ApiUriConstants.ALBUMS_BASE, album.getId()));
    }

    @GET
    @Path(ApiUriConstants.ID)
    public Response getAlbum(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Context Request request) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        Album album = albumService.findAndSetContextDependentFields(id, loggedUserId);
        return buildResponseUsingEtag(request, () -> {
            AlbumDTO albumDTO = albumDtoMapper.toDTO(album);
            return albumResourceMapper.toResource(albumDTO, getBaseUrl());
        });
    }

    @PUT
    @Path(ApiUriConstants.ID)
    @PreAuthorize("hasRole('MODERATOR')")
    public Response updateAlbum(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Valid ModAlbumForm modAlbumForm) {
        Album oldAlbum = albumService.findById(id);
        Album albumToUpdate = modAlbumFormMapper.mergeModel(oldAlbum, modAlbumForm);
        Album updatedAlbum = albumService.update(albumToUpdate);
        AlbumDTO albumDTO = albumDtoMapper.toDTO(updatedAlbum);
        AlbumResource albumResource = albumResourceMapper.toResource(albumDTO, getBaseUrl());
        return buildResponse(albumResource);
    } 

    @DELETE
    @Path(ApiUriConstants.ID)
    @PreAuthorize("hasRole('MODERATOR')")
    public Response deleteAlbum(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        albumService.delete(id);
        return buildNoContentResponse();
    }

    @GET
    @Path(ApiUriConstants.ALBUM_REVIEWS)
    public Response getAlbumReviews(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.USER_ID_PARAM_NAME) Long userId) {
        List<Review> reviews = new ArrayList<>();
        if (userId != null) reviews.add(reviewService.findAlbumReviewByUserId(userId, id, SecurityContextUtils.getCurrentUserId()));
        else reviews = reviewService.findAlbumReviewsPaginated(id, page, size, SecurityContextUtils.getCurrentUserId());
        List<ReviewDTO> reviewDTOs = reviewDtoMapper.toDTOList(reviews);
        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviewDTOs, getBaseUrl());
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                reviewResources, reviewService.countAll().intValue(), page, size, getBaseUrl(), ApiUriConstants.ALBUMS_BASE + ApiUriConstants.ALBUM_REVIEWS, ControllerUtils.itemReviewsCollectionLinks, id);
        
        return buildPaginatedResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.ALBUM_SONGS)
    public Response getAlbumSongs(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page, @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size) {
        List<Song> songs = songService.findByAlbumId(id);
        List<SongDTO> songDTOs = songDtoMapper.toDTOList(songs);
        List<SongResource> songResources = songResourceMapper.toResourceList(songDTOs, getBaseUrl());
        
        CollectionResource<SongResource> collection = collectionResourceMapper.createCollection(
                songResources, songService.countAll().intValue(), page, size, getBaseUrl(), ApiUriConstants.ALBUMS_BASE + ApiUriConstants.ALBUM_SONGS, ControllerUtils.albumSongsCollectionLinks, id);
        
        return buildPaginatedResponse(collection);
    }

    @POST
    @Path(ApiUriConstants.ALBUM_SONGS)
    @PreAuthorize("hasRole('MODERATOR')")
    public Response createAlbumSong(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @Valid ModSongForm modSongForm) {
        Song songInput = modSongFormMapper.toModel(modSongForm, id);
        Song song = songService.create(songInput);
        SongDTO songDTO = songDtoMapper.toDTO(song);
        SongResource songResource = songResourceMapper.toResource(songDTO, getBaseUrl());
        return buildCreatedResponse(songResource, buildResourceLocation(ApiUriConstants.SONGS_BASE, song.getId()));
    }

    @POST
    @Path(ApiUriConstants.ALBUM_FAVORITE)
    public Response addAlbumFavorite(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        userService.addFavoriteAlbum(loggedUserId, id);
        Album album = albumService.findAndSetContextDependentFields(id, loggedUserId);
        AlbumDTO albumDTO = albumDtoMapper.toDTO(album);
        AlbumResource albumResource = albumResourceMapper.toResource(albumDTO, getBaseUrl());
        // Location points to the user's favorite albums collection for this album
        return buildCreatedResponse(albumResource,
                buildNestedResourceLocation(ApiUriConstants.USERS_BASE, loggedUserId, "/favorites/albums", id));
    }

    @DELETE
    @Path(ApiUriConstants.ALBUM_FAVORITE)
    public Response removeAlbumFavorite(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        userService.removeFavoriteAlbum(SecurityContextUtils.getCurrentUserId(), id);
        return buildNoContentResponse();
    }
}
