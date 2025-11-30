package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.dto.AlbumDTO;
import ar.edu.itba.paw.api.dto.ArtistDTO;
import ar.edu.itba.paw.api.dto.ReviewDTO;
import ar.edu.itba.paw.api.dto.SongDTO;
import ar.edu.itba.paw.api.mapper.dto.AlbumDtoMapper;
import ar.edu.itba.paw.api.mapper.dto.ArtistDtoMapper;
import ar.edu.itba.paw.api.mapper.dto.SongDtoMapper;
import ar.edu.itba.paw.api.mapper.resource.ArtistResourceMapper;
import ar.edu.itba.paw.api.mapper.resource.CollectionResourceMapper;
import ar.edu.itba.paw.api.mapper.resource.ReviewResourceMapper;
import ar.edu.itba.paw.api.mapper.resource.SongResourceMapper;
import ar.edu.itba.paw.api.mapper.resource.AlbumResourceMapper;
import ar.edu.itba.paw.api.models.resources.ArtistResource;
import ar.edu.itba.paw.api.models.resources.AlbumResource;
import ar.edu.itba.paw.api.models.resources.CollectionResource;
import ar.edu.itba.paw.api.models.resources.ReviewResource;
import ar.edu.itba.paw.api.models.resources.SongResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.SecurityContextUtils;
import ar.edu.itba.paw.api.utils.ControllerUtils;
import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.services.ArtistService;
import ar.edu.itba.paw.services.AlbumService;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.services.SongService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.api.form.ReviewForm;
import ar.edu.itba.paw.api.form.ModAlbumForm;
import ar.edu.itba.paw.api.form.ModArtistForm;
import ar.edu.itba.paw.api.mapper.dto.ReviewFormMapper;
import ar.edu.itba.paw.api.mapper.dto.ReviewDtoMapper;
import ar.edu.itba.paw.api.mapper.dto.ModAlbumFormMapper;
import ar.edu.itba.paw.api.mapper.dto.ModArtistFormMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path(ApiUriConstants.ARTISTS_BASE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArtistController extends BaseController {

    @Autowired
    private ArtistService artistService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private SongService songService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SongResourceMapper songResourceMapper;

    @Autowired
    private AlbumResourceMapper albumResourceMapper;

    @Autowired
    private ArtistResourceMapper artistResourceMapper;

    @Autowired
    private ReviewResourceMapper reviewResourceMapper;

    @Autowired
    private CollectionResourceMapper collectionResourceMapper;

    @Autowired
    private ReviewFormMapper reviewFormMapper;

    @Autowired
    private ModAlbumFormMapper modAlbumFormMapper;

    @Autowired
    private ModArtistFormMapper modArtistFormMapper;

    @Autowired
    private ArtistDtoMapper artistDtoMapper;

    @Autowired
    private AlbumDtoMapper albumDtoMapper;

    @Autowired
    private SongDtoMapper songDtoMapper;

    @Autowired
    private ReviewDtoMapper reviewDtoMapper;

    @GET
    public Response getAllArtists(
            @QueryParam(ControllerUtils.SEARCH_PARAM_NAME) String search,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {

        if (search != null && !search.isEmpty()) return getArtistBySubstring(search, page, size);

        List<Artist> artists = artistService.findPaginated(filter, page, size);
        List<ArtistDTO> artistDTOs = artistDtoMapper.toDTOList(artists);
        List<ArtistResource> artistResources = artistResourceMapper.toResourceList(artistDTOs, getBaseUrl());
        Integer totalCount = artistService.countAll().intValue();
        
        CollectionResource<ArtistResource> collection = collectionResourceMapper.createCollection(
                artistResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.ARTISTS_BASE, ControllerUtils.artistsCollectionLinks);
        
        return buildResponse(collection);
    }

    private Response getArtistBySubstring(String substring, Integer page, Integer size) {
        List<Artist> artists = artistService.findByNameContaining(substring, page, size);
        List<ArtistDTO> artistDTOs = artistDtoMapper.toDTOList(artists);
        List<ArtistResource> artistResources = artistResourceMapper.toResourceList(artistDTOs, getBaseUrl());
        Integer totalCount = artistService.countAll().intValue();
        CollectionResource<ArtistResource> collection = collectionResourceMapper.createCollection(
                artistResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.ARTISTS_BASE, ControllerUtils.artistsCollectionLinks);
        return buildResponse(collection);
    }

    @POST
    @PreAuthorize("hasRole('MODERATOR')")
    public Response createArtist(@Valid ModArtistForm modArtistForm) {
        Artist artistInput = modArtistFormMapper.toModel(modArtistForm);
        Artist artist = artistService.create(artistInput);
        ArtistDTO artistDTO = artistDtoMapper.toDTO(artist);
        ArtistResource artistResource = artistResourceMapper.toResource(artistDTO, getBaseUrl());
        return buildCreatedResponse(artistResource);
    }

    @GET
    @Path(ApiUriConstants.ID)
    public Response getArtist(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        Artist artist = artistService.findById(id, loggedUserId);
        
        Boolean isReviewed = loggedUserId != null ? artistService.hasUserReviewed(loggedUserId, id) : false;
        Boolean isFavorite = loggedUserId != null ? userService.isArtistFavorite(loggedUserId, id) : false;
        
        ArtistDTO artistDTO = artistDtoMapper.toDTO(artist, isReviewed, isFavorite);
        ArtistResource artistResource = artistResourceMapper.toResource(artistDTO, getBaseUrl());
        return buildResponse(artistResource);
    }

    @PUT
    @Path(ApiUriConstants.ID)
    @PreAuthorize("hasRole('MODERATOR')")
    public Response updateArtist(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Valid ModArtistForm modArtistForm) {
        Artist artistInput = modArtistFormMapper.toModel(modArtistForm);
        artistInput.setId(id);
        Artist artist = artistService.update(artistInput);
        ArtistDTO artistDTO = artistDtoMapper.toDTO(artist);
        ArtistResource artistResource = artistResourceMapper.toResource(artistDTO, getBaseUrl());
        return buildResponse(artistResource);
    }

    @DELETE
    @Path(ApiUriConstants.ID)
    @PreAuthorize("hasRole('MODERATOR')")
    public Response deleteArtist(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        artistService.delete(id);
        return buildNoContentResponse();
    }

    @GET
    @Path(ApiUriConstants.ARTIST_REVIEWS)
    public Response getArtistReviews(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size) {

        Long loggedUserId = SecurityContextUtils.getCurrentUserId();

        List<Review> reviews = reviewService.findArtistReviewsPaginated(id, page, size, loggedUserId);
        List<ReviewDTO> reviewDTOs = reviewDtoMapper.toDTOList(reviews, loggedUserId, reviewService);
        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviewDTOs, getBaseUrl());
        Integer totalCount = reviewService.countAll().intValue();
        
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                reviewResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.ARTISTS_BASE + ApiUriConstants.ARTIST_REVIEWS, ControllerUtils.itemReviewsCollectionLinks, id);
        
        return buildResponse(collection);
    }

    /*
    @POST
    @Path(ApiUriConstants.ARTIST_REVIEWS)
    public Response createArtistReview(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @Valid ReviewForm reviewForm) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        Review reviewInput = reviewFormMapper.toModel(reviewForm, loggedUserId, id);
        reviewInput.setLikes(0);
        reviewInput.setBlocked(false);
        Review review = reviewService.create(reviewInput);
        ReviewDTO reviewDTO = reviewDtoMapper.toDTO(review, true);
        ReviewResource reviewResource = reviewResourceMapper.toResource(reviewDTO, getBaseUrl());
        return buildResponse(reviewResource);
    }
     */

    @GET
    @Path(ApiUriConstants.ARTIST_ALBUMS)
    public Response getArtistAlbums(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size) {

        List<Album> albums = albumService.findByArtistId(id);
        List<AlbumDTO> albumDTOs = albumDtoMapper.toDTOList(albums);
        List<AlbumResource> albumResources = albumResourceMapper.toResourceList(albumDTOs, getBaseUrl());
        Integer totalCount = albumService.countAll().intValue();
        
        CollectionResource<AlbumResource> collection = collectionResourceMapper.createCollection(
                albumResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.ARTISTS_BASE + ApiUriConstants.ARTIST_ALBUMS, ControllerUtils.artistAlbumsCollectionLinks, id);
        
        return buildResponse(collection);
    }

    @POST
    @Path(ApiUriConstants.ARTIST_ALBUMS)
    @PreAuthorize("hasRole('MODERATOR')")
    public Response createArtistAlbum(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @Valid ModAlbumForm modAlbumForm) {
        Album albumInput = modAlbumFormMapper.toModel(modAlbumForm);
        albumInput.setArtist(new Artist(id));
        Album album = albumService.create(albumInput);
        AlbumDTO albumDTO = albumDtoMapper.toDTO(album);
        AlbumResource albumResource = albumResourceMapper.toResource(albumDTO, getBaseUrl());
        return buildResponse(albumResource);
    }

    @GET
    @Path(ApiUriConstants.ARTIST_SONGS)
    public Response getArtistSongs(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, 
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page, 
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.POPULAR_FILTER_STRING) FilterType filter) {
        List<Song> songs = songService.findByArtistId(id, filter, page, size);
        List<SongDTO> songDTOs = songDtoMapper.toDTOList(songs);
        List<SongResource> songResources = songResourceMapper.toResourceList(songDTOs, getBaseUrl());
        Integer totalCount = songService.countAll().intValue();
        CollectionResource<SongResource> collection = collectionResourceMapper.createCollection(
                songResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.ARTISTS_BASE + ApiUriConstants.ARTIST_SONGS, ControllerUtils.artistSongsCollectionLinks, id);
        return buildResponse(collection);
    }
    
    @POST
    @Path(ApiUriConstants.ARTIST_FAVORITE)
    public Response addArtistFavorite(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        userService.addFavoriteArtist(loggedUserId, id);
        Artist artist = artistService.findById(id, loggedUserId);
        ArtistDTO artistDTO = artistDtoMapper.toDTO(artist, false, true);
        return buildCreatedResponse(artistResourceMapper.toResource(artistDTO, getBaseUrl()));
    }

    @DELETE
    @Path(ApiUriConstants.ARTIST_FAVORITE)
    public Response removeArtistFavorite(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        userService.removeFavoriteArtist(SecurityContextUtils.getCurrentUserId(), id);
        return buildNoContentResponse();
    }
}
