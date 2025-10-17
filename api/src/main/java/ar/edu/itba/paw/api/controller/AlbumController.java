package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.mapper.AlbumResourceMapper;
import ar.edu.itba.paw.api.mapper.CollectionResourceMapper;
import ar.edu.itba.paw.api.mapper.ReviewResourceMapper;
import ar.edu.itba.paw.api.mapper.SongResourceMapper;
import ar.edu.itba.paw.api.resources.AlbumResource;
import ar.edu.itba.paw.api.resources.CollectionResource;
import ar.edu.itba.paw.api.resources.ReviewResource;
import ar.edu.itba.paw.api.resources.SongResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.models.dtos.AlbumDTO;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
import ar.edu.itba.paw.models.dtos.SongDTO;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.services.AlbumService;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.services.SongService;
import org.springframework.beans.factory.annotation.Autowired;

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
    private AlbumResourceMapper albumResourceMapper;

    @Autowired
    private ReviewResourceMapper reviewResourceMapper;

    @Autowired
    private SongResourceMapper songResourceMapper;

    @Autowired
    private CollectionResourceMapper collectionResourceMapper;

    @GET
    public Response getAllAlbums(
            @QueryParam("search") String search,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("filter") @DefaultValue("FIRST") FilterType filter) {

        if (search != null && !search.isEmpty()) return getAlbumBySubstring(search, page, size);

        List<AlbumDTO> albumDTOs = albumService.findPaginated(filter, page, size);
        List<AlbumResource> albumResources = albumResourceMapper.toResourceList(albumDTOs, getBaseUrl());
        Long totalCount = albumService.countAll();
        
        CollectionResource<AlbumResource> collection = collectionResourceMapper.createCollection(
                albumResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.ALBUMS_BASE);
        
        return buildResponse(collection);
    }

    private Response getAlbumBySubstring(String substring, int page, int size) {
        List<AlbumDTO> albums = albumService.findByTitleContaining(substring, page, size);
        List<AlbumResource> albumResources = albumResourceMapper.toResourceList(albums, getBaseUrl());
        Long totalCount = albumService.countAll();
        CollectionResource<AlbumResource> collection = collectionResourceMapper.createCollection(
                albumResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.ALBUMS_BASE);
        return buildResponse(collection);
    }

    @POST
    public Response createAlbum(
            @Valid AlbumDTO albumDTO) {
        
        if (albumDTO.getArtistId() == 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Artist ID is required")
                    .build();
        }

        AlbumDTO responseDTO = albumService.create(albumDTO);
        AlbumResource albumResource = albumResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildCreatedResponse(albumResource);
    }

    @GET
    @Path(ApiUriConstants.ID)
    public Response getAlbum(@PathParam("id") Long id) {
        AlbumDTO albumDTO = albumService.findById(id);
        AlbumResource albumResource = albumResourceMapper.toResource(albumDTO, getBaseUrl());
        return buildResponse(albumResource);
    }

    @PUT
    @Path(ApiUriConstants.ID)
    public Response updateAlbum(@PathParam("id") Long id, @Valid AlbumDTO albumDTO) {
        albumDTO.setId(id);
        AlbumDTO responseDTO = albumService.update(albumDTO);
        AlbumResource albumResource = albumResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildResponse(albumResource);
    }

    @DELETE
    @Path(ApiUriConstants.ID)
    public Response deleteAlbum(@PathParam("id") Long id) {
        albumService.delete(id);
        return buildNoContentResponse();
    }

    @GET
    @Path(ApiUriConstants.ALBUM_REVIEWS)
    public Response getAlbumReviews(
            @PathParam("id") Long id,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("loggedUserId") @DefaultValue("1") Long loggedUserId) {
        // TODO: Obtener loggedUserId del contexto de seguridad
        
        List<ReviewDTO> reviews = reviewService.findAlbumReviewsPaginated(id, page, size, loggedUserId);
        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviews, getBaseUrl());
        
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                reviewResources, reviewService.countAll(), page, size, getBaseUrl(), ApiUriConstants.REVIEWS_BASE);
        
        return buildResponse(collection);
    }

    @POST
    @Path(ApiUriConstants.ALBUM_REVIEWS)
    public Response createAlbumReview(
            @PathParam("id") Long id,
            @Valid ReviewDTO reviewDTO) {
        reviewDTO.setItemId(id);
        reviewDTO.setItemType("Album");
        ReviewDTO responseDTO = reviewService.create(reviewDTO);
        ReviewResource reviewResource = reviewResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildResponse(reviewResource);
    }

    @GET
    @Path(ApiUriConstants.ALBUM_SONGS)
    public Response getAlbumSongs(@PathParam("id") Long id, @QueryParam("page") @DefaultValue("1") int page, @QueryParam("size") @DefaultValue("20") int size) {
        List<SongDTO> songDTOs = songService.findByAlbumId(id);
        List<SongResource> songResources = songResourceMapper.toResourceList(songDTOs, getBaseUrl());
        
        CollectionResource<SongResource> collection = collectionResourceMapper.createCollection(
                songResources, songService.countAll(), page, size, getBaseUrl(), ApiUriConstants.SONGS_BASE);
        
        return buildResponse(collection);
    }

    @POST
    @Path(ApiUriConstants.ALBUM_SONGS)
    public Response createAlbumSong(
            @PathParam("id") Long id,
            @Valid SongDTO songDTO) {
        songDTO.setAlbumId(id);
        SongDTO responseDTO = songService.create(songDTO);
        SongResource songResource = songResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildResponse(songResource);
    }
}

