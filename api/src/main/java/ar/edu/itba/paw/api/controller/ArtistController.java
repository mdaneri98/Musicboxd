package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.mapper.ArtistResourceMapper;
import ar.edu.itba.paw.api.mapper.CollectionResourceMapper;
import ar.edu.itba.paw.api.mapper.ReviewResourceMapper;
import ar.edu.itba.paw.api.mapper.SongResourceMapper;
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
import ar.edu.itba.paw.api.mapper.AlbumResourceMapper;
import ar.edu.itba.paw.api.models.resources.AlbumResource;
import ar.edu.itba.paw.api.models.resources.SongResource;
import ar.edu.itba.paw.models.dtos.SongDTO;
import ar.edu.itba.paw.services.SongService;
import ar.edu.itba.paw.api.models.links.managers.CollectionLinkManager;
import ar.edu.itba.paw.services.UserService;

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
    private AlbumResourceMapper albumResourceMapper;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private SongService songService;

    @Autowired
    private UserService userService;

    @Autowired
    private SongResourceMapper songResourceMapper;

    @Autowired
    private ArtistResourceMapper artistResourceMapper;

    @Autowired
    private ReviewResourceMapper reviewResourceMapper;

    @Autowired
    private CollectionResourceMapper collectionResourceMapper;


    private CollectionLinkManager artistsCollectionLinks = new CollectionLinkManager(true, false, false, true, true);
    private CollectionLinkManager reviewsCollectionLinks = new CollectionLinkManager(true, false, false, false, true);
    private CollectionLinkManager albumsCollectionLinks = new CollectionLinkManager(true, false, false, false, true);
    private CollectionLinkManager songsCollectionLinks = new CollectionLinkManager(false, false, false, false, true);

    @GET
    public Response getAllArtists(
            @QueryParam("search") String search,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("filter") @DefaultValue("FIRST") FilterType filter) {

        if (search != null && !search.isEmpty()) return getArtistBySubstring(search, page, size);

        List<ArtistDTO> artistDTOs = artistService.findPaginated(filter, page, size);
        List<ArtistResource> artistResources = artistResourceMapper.toResourceList(artistDTOs, getBaseUrl());
        Long totalCount = artistService.countAll();
        
        CollectionResource<ArtistResource> collection = collectionResourceMapper.createCollection(
                artistResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.ARTISTS_BASE, artistsCollectionLinks, null);
        
        return buildResponse(collection);
    }

    private Response getArtistBySubstring(String substring, int page, int size) {
        List<ArtistDTO> artists = artistService.findByNameContaining(substring, page, size);
        List<ArtistResource> artistResources = artistResourceMapper.toResourceList(artists, getBaseUrl());
        Long totalCount = artistService.countAll();
        CollectionResource<ArtistResource> collection = collectionResourceMapper.createCollection(
                artistResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.ARTISTS_BASE, artistsCollectionLinks, null);
        return buildResponse(collection);
    }

    @POST
    public Response createArtist(@Valid ArtistDTO artistDTO) {
        ArtistDTO responseDTO = artistService.create(artistDTO);
        ArtistResource artistResource = artistResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildCreatedResponse(artistResource);
    }

    @GET
    @Path(ApiUriConstants.ID)
    public Response getArtist(@PathParam("id") Long id) {
        ArtistDTO artistDTO = artistService.findById(id);
        ArtistResource artistResource = artistResourceMapper.toResource(artistDTO, getBaseUrl());
        return buildResponse(artistResource);
    }

    @PUT
    @Path(ApiUriConstants.ID)
    public Response updateArtist(@PathParam("id") Long id, @Valid ArtistDTO artistDTO) {
        artistDTO.setId(id);
        ArtistDTO responseDTO = artistService.update(artistDTO);
        ArtistResource artistResource = artistResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildResponse(artistResource);
    }

    @DELETE
    @Path(ApiUriConstants.ID)
    public Response deleteArtist(@PathParam("id") Long id) {
        artistService.delete(id);
        return buildNoContentResponse();
    }

    @GET
    @Path(ApiUriConstants.ARTIST_REVIEWS)
    public Response getArtistReviews(
            @PathParam("id") Long id,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        Long loggedUserId = SecurityContextUtils.getCurrentUserId();

        List<ReviewDTO> reviews = reviewService.findArtistReviewsPaginated(id, page, size, loggedUserId);
        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviews, getBaseUrl());
        Long totalCount = reviewService.countAll();
        
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                reviewResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.ARTISTS_BASE + ApiUriConstants.ARTIST_REVIEWS, reviewsCollectionLinks, id);
        
        return buildResponse(collection);
    }


    @POST
    @Path(ApiUriConstants.ARTIST_REVIEWS)
    public Response createArtistReview(
            @PathParam("id") Long id,
            @Valid ReviewDTO reviewDTO) {
        reviewDTO.setItemId(id);
        reviewDTO.setItemType("Artist");
        ReviewDTO responseDTO = reviewService.createArtistReview(reviewDTO);
        ReviewResource reviewResource = reviewResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildResponse(reviewResource);
    }

    @GET
    @Path(ApiUriConstants.ARTIST_ALBUMS)
    public Response getArtistAlbums(
            @PathParam("id") Long id,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        List<AlbumDTO> albums = albumService.findByArtistId(id);
        List<AlbumResource> albumResources = albumResourceMapper.toResourceList(albums, getBaseUrl());
        Long totalCount = albumService.countAll();
        
        CollectionResource<AlbumResource> collection = collectionResourceMapper.createCollection(
                albumResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.ARTISTS_BASE + ApiUriConstants.ARTIST_ALBUMS, albumsCollectionLinks, id);
        
        return buildResponse(collection);
    }

    @POST
    @Path(ApiUriConstants.ARTIST_ALBUMS)
    public Response createArtistAlbum(
            @PathParam("id") Long id,
            @Valid AlbumDTO albumDTO) {
        albumDTO.setArtistId(id);
        AlbumDTO responseDTO = albumService.create(albumDTO);
        AlbumResource albumResource = albumResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildResponse(albumResource);
    }

    @GET
    @Path(ApiUriConstants.ARTIST_SONGS)
    public Response getArtistSongs(@PathParam("id") Long id, @QueryParam("page") @DefaultValue("1") int page, @QueryParam("size") @DefaultValue("20") int size) {
        List<SongDTO> songs = songService.findByArtistId(id, page, size);
        List<SongResource> songResources = songResourceMapper.toResourceList(songs, getBaseUrl());
        Long totalCount = songService.countAll();
        CollectionResource<SongResource> collection = collectionResourceMapper.createCollection(
                songResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.ARTISTS_BASE + ApiUriConstants.ARTIST_SONGS, songsCollectionLinks, id);
        return buildResponse(collection);
    }
    
    @POST
    @Path(ApiUriConstants.ARTIST_FAVORITE)
    public Response addArtistFavorite(@PathParam("id") Long id) {
        userService.addFavoriteArtist(SecurityContextUtils.getCurrentUserId(), id);
        return buildCreatedResponse(artistService.findById(id));
    }

    @DELETE
    @Path(ApiUriConstants.ARTIST_FAVORITE)
    public Response removeArtistFavorite(@PathParam("id") Long id) {
        userService.removeFavoriteArtist(SecurityContextUtils.getCurrentUserId(), id);
        return buildNoContentResponse();
    }
}

