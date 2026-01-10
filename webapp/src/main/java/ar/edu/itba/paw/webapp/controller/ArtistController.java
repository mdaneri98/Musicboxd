package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.AlbumDTO;
import ar.edu.itba.paw.webapp.dto.ArtistDTO;
import ar.edu.itba.paw.webapp.dto.ReviewDTO;
import ar.edu.itba.paw.webapp.dto.SongDTO;
import ar.edu.itba.paw.webapp.mapper.dto.AlbumDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.ArtistDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.SongDtoMapper;
import ar.edu.itba.paw.webapp.mapper.resource.ArtistResourceMapper;
import ar.edu.itba.paw.webapp.mapper.resource.CollectionResourceMapper;
import ar.edu.itba.paw.webapp.mapper.resource.ReviewResourceMapper;
import ar.edu.itba.paw.webapp.mapper.resource.SongResourceMapper;
import ar.edu.itba.paw.webapp.mapper.resource.AlbumResourceMapper;
import ar.edu.itba.paw.webapp.models.resources.ArtistResource;
import ar.edu.itba.paw.webapp.models.resources.AlbumResource;
import ar.edu.itba.paw.webapp.models.resources.CollectionResource;
import ar.edu.itba.paw.webapp.models.resources.ReviewResource;
import ar.edu.itba.paw.webapp.models.resources.SongResource;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.webapp.utils.SecurityContextUtils;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
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
import ar.edu.itba.paw.webapp.form.ModAlbumForm;
import ar.edu.itba.paw.webapp.form.ModArtistForm;
import ar.edu.itba.paw.webapp.mapper.dto.ReviewDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.ModAlbumFormMapper;
import ar.edu.itba.paw.webapp.mapper.dto.ModArtistFormMapper;
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
        List<Artist> artists;
        if (search != null && !search.isEmpty()) artists = artistService.findByNameContaining(search, page, size);
        else artists = artistService.findPaginated(filter, page, size);
        List<ArtistDTO> artistDTOs = artistDtoMapper.toDTOList(artists);
        List<ArtistResource> artistResources = artistResourceMapper.toResourceList(artistDTOs, getBaseUrl());
        CollectionResource<ArtistResource> collection = collectionResourceMapper.createCollection(
                artistResources, artistService.countAll().intValue(), page, size, getBaseUrl(), ApiUriConstants.ARTISTS_BASE, ControllerUtils.artistsCollectionLinks);
        
        return buildPaginatedResponse(collection);
    }

    @POST
    @PreAuthorize("hasRole('MODERATOR')")
    public Response createArtist(@Valid ModArtistForm modArtistForm) {
        Artist artistInput = modArtistFormMapper.toModel(modArtistForm);
        Artist artist = artistService.create(artistInput);
        ArtistDTO artistDTO = artistDtoMapper.toDTO(artist);
        ArtistResource artistResource = artistResourceMapper.toResource(artistDTO, getBaseUrl());
        return buildCreatedResponse(artistResource, buildResourceLocation(ApiUriConstants.ARTISTS_BASE, artist.getId()));
    }

    @GET
    @Path(ApiUriConstants.ID)
    public Response getArtist(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Context Request request) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        Artist artist = artistService.findAndSetContextDependentFields(id, loggedUserId);
        return buildResponseUsingEtag(request, () -> {
            ArtistDTO artistDTO = artistDtoMapper.toDTO(artist);
            return artistResourceMapper.toResource(artistDTO, getBaseUrl());
        });
    }

    @PUT
    @Path(ApiUriConstants.ID)
    @PreAuthorize("hasRole('MODERATOR')")
    public Response updateArtist(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Valid ModArtistForm modArtistForm) {
        Artist form = modArtistFormMapper.toModel(modArtistForm);
        Artist updatedArtist = artistService.update(form);
        ArtistDTO artistDTO = artistDtoMapper.toDTO(updatedArtist);
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
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.USER_ID_PARAM_NAME) Long userId) {
        List<Review> reviews = new ArrayList<>();
        if (userId != null) reviews.add(reviewService.findArtistReviewByUserId(userId, id, SecurityContextUtils.getCurrentUserId()));
        else reviews = reviewService.findArtistReviewsPaginated(id, page, size, SecurityContextUtils.getCurrentUserId());
        List<ReviewDTO> reviewDTOs = reviewDtoMapper.toDTOList(reviews);
        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviewDTOs, getBaseUrl());
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                reviewResources, reviewService.countAll().intValue(), page, size, getBaseUrl(), ApiUriConstants.ARTISTS_BASE + ApiUriConstants.ARTIST_REVIEWS, ControllerUtils.itemReviewsCollectionLinks, id);
        return buildPaginatedResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.ARTIST_ALBUMS)
    public Response getArtistAlbums(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size) {
        List<Album> albums = albumService.findByArtistId(id);
        List<AlbumDTO> albumDTOs = albumDtoMapper.toDTOList(albums);
        List<AlbumResource> albumResources = albumResourceMapper.toResourceList(albumDTOs, getBaseUrl());
        CollectionResource<AlbumResource> collection = collectionResourceMapper.createCollection(
                albumResources, albumService.countAll().intValue(), page, size, getBaseUrl(), ApiUriConstants.ARTISTS_BASE + ApiUriConstants.ARTIST_ALBUMS, ControllerUtils.artistAlbumsCollectionLinks, id);
        return buildPaginatedResponse(collection);
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
        return buildCreatedResponse(albumResource, buildResourceLocation(ApiUriConstants.ALBUMS_BASE, album.getId()));
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
        CollectionResource<SongResource> collection = collectionResourceMapper.createCollection(
                songResources, songService.countAll().intValue(), page, size, getBaseUrl(), ApiUriConstants.ARTISTS_BASE + ApiUriConstants.ARTIST_SONGS, ControllerUtils.artistSongsCollectionLinks, id);
        return buildPaginatedResponse(collection);
    }
    

}
