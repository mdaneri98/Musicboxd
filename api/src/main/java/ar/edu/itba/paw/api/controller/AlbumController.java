package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.dto.AlbumDTO;
import ar.edu.itba.paw.api.dto.ReviewDTO;
import ar.edu.itba.paw.api.dto.SongDTO;
import ar.edu.itba.paw.api.mapper.dto.AlbumDtoMapper;
import ar.edu.itba.paw.api.mapper.dto.SongDtoMapper;
import ar.edu.itba.paw.api.mapper.resource.AlbumResourceMapper;
import ar.edu.itba.paw.api.mapper.resource.CollectionResourceMapper;
import ar.edu.itba.paw.api.mapper.resource.ReviewResourceMapper;
import ar.edu.itba.paw.api.mapper.resource.SongResourceMapper;
import ar.edu.itba.paw.api.models.resources.AlbumResource;
import ar.edu.itba.paw.api.models.resources.CollectionResource;
import ar.edu.itba.paw.api.models.resources.ReviewResource;
import ar.edu.itba.paw.api.models.resources.SongResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.SecurityContextUtils;
import ar.edu.itba.paw.api.utils.ControllerUtils;
import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.services.AlbumService;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.services.SongService;
import ar.edu.itba.paw.api.form.ModAlbumForm;
import ar.edu.itba.paw.api.form.ReviewForm;
import ar.edu.itba.paw.api.form.ModSongForm;
import ar.edu.itba.paw.api.mapper.dto.ModAlbumFormMapper;
import ar.edu.itba.paw.api.mapper.dto.ReviewFormMapper;
import ar.edu.itba.paw.api.mapper.dto.ReviewDtoMapper;
import ar.edu.itba.paw.api.mapper.dto.ModSongFormMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

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
    private ReviewFormMapper reviewFormMapper;

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

        if (search != null && !search.isEmpty()) return getAlbumBySubstring(search, page, size);

        List<Album> albums = albumService.findPaginated(filter, page, size);
        List<AlbumDTO> albumDTOs = albumDtoMapper.toDTOList(albums);
        List<AlbumResource> albumResources = albumResourceMapper.toResourceList(albumDTOs, getBaseUrl());
        Integer totalCount = albumService.countAll().intValue();
        CollectionResource<AlbumResource> collection = collectionResourceMapper.createCollection(
                albumResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.ALBUMS_BASE, ControllerUtils.albumsCollectionLinks);
        return buildResponse(collection);
    }

    private Response getAlbumBySubstring(String substring, Integer page, Integer size) {
        List<Album> albums = albumService.findByTitleContaining(substring, page, size);
        List<AlbumDTO> albumDTOs = albumDtoMapper.toDTOList(albums);
        List<AlbumResource> albumResources = albumResourceMapper.toResourceList(albumDTOs, getBaseUrl());
        Integer totalCount = albumService.countAll().intValue();
        CollectionResource<AlbumResource> collection = collectionResourceMapper.createCollection(
                albumResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.ALBUMS_BASE, ControllerUtils.albumsCollectionLinks);
        return buildResponse(collection);
    }

    @POST
    @PreAuthorize("hasRole('MODERATOR')")
    public Response createAlbum(@Valid ModAlbumForm modAlbumForm) {
        Album albumInput = modAlbumFormMapper.toModel(modAlbumForm);
        Album album = albumService.create(albumInput);
        AlbumDTO albumDTO = albumDtoMapper.toDTO(album);
        AlbumResource albumResource = albumResourceMapper.toResource(albumDTO, getBaseUrl());
        return buildCreatedResponse(albumResource);
    }

    @GET
    @Path(ApiUriConstants.ID)
    public Response getAlbum(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        Album album = albumService.findById(id);
        Boolean isReviewed = loggedUserId != null ? albumService.hasUserReviewed(loggedUserId, id) : false;
        Boolean isFavorite = loggedUserId != null ? userService.isAlbumFavorite(loggedUserId, id) : false;
        AlbumDTO albumDTO = albumDtoMapper.toDTO(album, isReviewed, isFavorite);
        AlbumResource albumResource = albumResourceMapper.toResource(albumDTO, getBaseUrl());
        return buildResponse(albumResource);
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
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size) {

        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        List<Review> reviews = reviewService.findAlbumReviewsPaginated(id, page, size, loggedUserId);
        List<ReviewDTO> reviewDTOs = reviewDtoMapper.toDTOList(reviews, loggedUserId, reviewService);
        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviewDTOs, getBaseUrl());
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                reviewResources, reviewService.countAll().intValue(), page, size, getBaseUrl(), ApiUriConstants.ALBUMS_BASE + ApiUriConstants.ALBUM_REVIEWS, ControllerUtils.itemReviewsCollectionLinks, id);
        
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.ALBUM_SONGS)
    public Response getAlbumSongs(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page, @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size) {
        List<Song> songs = songService.findByAlbumId(id);
        List<SongDTO> songDTOs = songDtoMapper.toDTOList(songs);
        List<SongResource> songResources = songResourceMapper.toResourceList(songDTOs, getBaseUrl());
        
        CollectionResource<SongResource> collection = collectionResourceMapper.createCollection(
                songResources, songService.countAll().intValue(), page, size, getBaseUrl(), ApiUriConstants.ALBUMS_BASE + ApiUriConstants.ALBUM_SONGS, ControllerUtils.albumSongsCollectionLinks, id);
        
        return buildResponse(collection);
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
        return buildResponse(songResource);
    }

    @POST
    @Path(ApiUriConstants.ALBUM_FAVORITE)
    public Response addAlbumFavorite(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        userService.addFavoriteAlbum(loggedUserId, id);
        Album album = albumService.findById(id);
        Boolean isReviewed = loggedUserId != null ? albumService.hasUserReviewed(loggedUserId, id) : false;
        Boolean isFavorite = loggedUserId != null ? userService.isAlbumFavorite(loggedUserId, id) : false;
        AlbumDTO albumDTO = albumDtoMapper.toDTO(album, isReviewed, isFavorite);
        return buildCreatedResponse(albumResourceMapper.toResource(albumDTO, getBaseUrl()));
    }

    @DELETE
    @Path(ApiUriConstants.ALBUM_FAVORITE)
    public Response removeAlbumFavorite(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        userService.removeFavoriteAlbum(SecurityContextUtils.getCurrentUserId(), id);
        return buildNoContentResponse();
    }
}
