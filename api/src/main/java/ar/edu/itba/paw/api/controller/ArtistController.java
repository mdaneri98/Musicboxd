package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.mapper.resource.ArtistResourceMapper;
import ar.edu.itba.paw.api.mapper.resource.CollectionResourceMapper;
import ar.edu.itba.paw.api.mapper.resource.ReviewResourceMapper;
import ar.edu.itba.paw.api.mapper.resource.SongResourceMapper;
import ar.edu.itba.paw.api.models.resources.ArtistResource;
import ar.edu.itba.paw.api.models.resources.CollectionResource;
import ar.edu.itba.paw.api.models.resources.ReviewResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.SecurityContextUtils;
import ar.edu.itba.paw.models.dtos.ArtistDTO;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
import ar.edu.itba.paw.services.ArtistService;
import ar.edu.itba.paw.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import ar.edu.itba.paw.models.dtos.AlbumDTO;
import ar.edu.itba.paw.services.AlbumService;
import ar.edu.itba.paw.api.mapper.resource.AlbumResourceMapper;
import ar.edu.itba.paw.api.models.resources.AlbumResource;
import ar.edu.itba.paw.api.models.resources.SongResource;
import ar.edu.itba.paw.models.dtos.SongDTO;
import ar.edu.itba.paw.services.SongService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.api.form.ReviewForm;
import ar.edu.itba.paw.api.form.ModAlbumForm;
import ar.edu.itba.paw.api.form.ModArtistForm;
import ar.edu.itba.paw.api.mapper.dto.ReviewFormMapper;
import ar.edu.itba.paw.api.mapper.dto.ModAlbumFormMapper;
import ar.edu.itba.paw.api.mapper.dto.ModArtistFormMapper;
import ar.edu.itba.paw.api.utils.ControllerUtils;
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

    @GET
    public Response getAllArtists(
            @QueryParam(ControllerUtils.SEARCH_PARAM_NAME) String search,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {

        if (search != null && !search.isEmpty()) return getArtistBySubstring(search, page, size);

        List<ArtistDTO> artistDTOs = artistService.findPaginated(filter, page, size);
        List<ArtistResource> artistResources = artistResourceMapper.toResourceList(artistDTOs, getBaseUrl());
        Integer totalCount = artistService.countAll().intValue();
        
        CollectionResource<ArtistResource> collection = collectionResourceMapper.createCollection(
                artistResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.ARTISTS_BASE, ControllerUtils.artistsCollectionLinks);
        
        return buildResponse(collection);
    }

    private Response getArtistBySubstring(String substring, Integer page, Integer size) {
        List<ArtistDTO> artists = artistService.findByNameContaining(substring, page, size);
        List<ArtistResource> artistResources = artistResourceMapper.toResourceList(artists, getBaseUrl());
        Integer totalCount = artistService.countAll().intValue();
        CollectionResource<ArtistResource> collection = collectionResourceMapper.createCollection(
                artistResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.ARTISTS_BASE, ControllerUtils.artistsCollectionLinks);
        return buildResponse(collection);
    }

    @POST
    @PreAuthorize("hasRole('MODERATOR')")
    public Response createArtist(@Valid ModArtistForm modArtistForm) {
        ArtistDTO artistDTO = modArtistFormMapper.toDTO(modArtistForm);
        ArtistDTO responseDTO = artistService.create(artistDTO);
        ArtistResource artistResource = artistResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildCreatedResponse(artistResource);
    }

    @GET
    @Path(ApiUriConstants.ID)
    public Response getArtist(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        ArtistDTO artistDTO = artistService.findById(id, SecurityContextUtils.getCurrentUserId());
        ArtistResource artistResource = artistResourceMapper.toResource(artistDTO, getBaseUrl());
        return buildResponse(artistResource);
    }

    @PUT
    @Path(ApiUriConstants.ID)
    @PreAuthorize("hasRole('MODERATOR')")
    public Response updateArtist(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Valid ModArtistForm modArtistForm) {
        ArtistDTO artistDTO = modArtistFormMapper.toDTO(modArtistForm);
        artistDTO.setImageId(modArtistForm.getArtistImgId());
        artistDTO.setId(id);
        ArtistDTO responseDTO = artistService.update(artistDTO);
        ArtistResource artistResource = artistResourceMapper.toResource(responseDTO, getBaseUrl());
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

        List<ReviewDTO> reviews = reviewService.findArtistReviewsPaginated(id, page, size, loggedUserId);
        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviews, getBaseUrl());
        Integer totalCount = reviewService.countAll().intValue();
        
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                reviewResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.ARTISTS_BASE + ApiUriConstants.ARTIST_REVIEWS, ControllerUtils.itemReviewsCollectionLinks, id);
        
        return buildResponse(collection);
    }


    @POST
    @Path(ApiUriConstants.ARTIST_REVIEWS)
    public Response createArtistReview(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @Valid ReviewForm reviewForm) {
        ReviewDTO reviewDTO = reviewFormMapper.toDTO(reviewForm);
        reviewDTO.setUserId(SecurityContextUtils.getCurrentUserId());
        reviewDTO.setIsLiked(true);
        reviewDTO.setLikes(0);
        reviewDTO.setIsBlocked(false);
        ReviewDTO responseDTO = reviewService.create(reviewDTO);
        ReviewResource reviewResource = reviewResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildResponse(reviewResource);
    }

    @GET
    @Path(ApiUriConstants.ARTIST_ALBUMS)
    public Response getArtistAlbums(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size) {

        List<AlbumDTO> albums = albumService.findByArtistId(id);
        List<AlbumResource> albumResources = albumResourceMapper.toResourceList(albums, getBaseUrl());
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
        AlbumDTO albumDTO = modAlbumFormMapper.toDTO(modAlbumForm);
        albumDTO.setArtistId(id);
        AlbumDTO responseDTO = albumService.create(albumDTO);
        AlbumResource albumResource = albumResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildResponse(albumResource);
    }

    @GET
    @Path(ApiUriConstants.ARTIST_SONGS)
    public Response getArtistSongs(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, 
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page, 
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.POPULAR_FILTER_STRING) FilterType filter) {
        List<SongDTO> songs = songService.findByArtistId(id, filter, page, size);
        List<SongResource> songResources = songResourceMapper.toResourceList(songs, getBaseUrl());
        Integer totalCount = songService.countAll().intValue();
        CollectionResource<SongResource> collection = collectionResourceMapper.createCollection(
                songResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.ARTISTS_BASE + ApiUriConstants.ARTIST_SONGS, ControllerUtils.artistSongsCollectionLinks, id);
        return buildResponse(collection);
    }
    
    @POST
    @Path(ApiUriConstants.ARTIST_FAVORITE)
    public Response addArtistFavorite(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        userService.addFavoriteArtist(SecurityContextUtils.getCurrentUserId(), id);
        return buildCreatedResponse(artistResourceMapper.toResource(artistService.findById(id, SecurityContextUtils.getCurrentUserId()), getBaseUrl()));
    }

    @DELETE
    @Path(ApiUriConstants.ARTIST_FAVORITE)
    public Response removeArtistFavorite(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        userService.removeFavoriteArtist(SecurityContextUtils.getCurrentUserId(), id);
        return buildNoContentResponse();
    }
}

