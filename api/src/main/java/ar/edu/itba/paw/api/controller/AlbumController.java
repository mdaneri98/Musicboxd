package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.mapper.AlbumResourceMapper;
import ar.edu.itba.paw.api.mapper.CollectionResourceMapper;
import ar.edu.itba.paw.api.mapper.ReviewResourceMapper;
import ar.edu.itba.paw.api.mapper.SongResourceMapper;
import ar.edu.itba.paw.api.models.AlbumResource;
import ar.edu.itba.paw.api.models.CollectionResource;
import ar.edu.itba.paw.api.models.ReviewResource;
import ar.edu.itba.paw.api.models.SongResource;
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
            @QueryParam("artistId") Long artistId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("filter") @DefaultValue("FIRST") FilterType filter) {
        
        List<AlbumDTO> albumDTOs = albumService.findPaginated(filter, page, size);
        List<AlbumResource> albumResources = albumResourceMapper.toResourceList(albumDTOs, getBaseUrl());
        
        CollectionResource<AlbumResource> collection = collectionResourceMapper.createCollection(
                albumResources, getBaseUrl(), ApiUriConstants.ALBUMS_BASE);
        
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.ALBUM_BY_ID)
    public Response getAlbum(@PathParam("id") Long id) {
        AlbumDTO albumDTO = albumService.findById(id);
        AlbumResource albumResource = albumResourceMapper.toResource(albumDTO, getBaseUrl());
        return buildResponse(albumResource);
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

    @PUT
    @Path(ApiUriConstants.ALBUM_BY_ID)
    public Response updateAlbum(@PathParam("id") Long id, @Valid AlbumDTO albumDTO) {
        albumDTO.setId(id);
        AlbumDTO responseDTO = albumService.update(albumDTO);
        AlbumResource albumResource = albumResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildResponse(albumResource);
    }

    @DELETE
    @Path(ApiUriConstants.ALBUM_BY_ID)
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
            @QueryParam("loggedUserId") Long loggedUserId) {
        // TODO: Obtener loggedUserId del contexto de seguridad
        
        List<ReviewDTO> reviews = reviewService.findAlbumReviewsPaginated(id, page, size, loggedUserId);
        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviews, getBaseUrl());
        
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                reviewResources, getBaseUrl(), ApiUriConstants.REVIEWS_BASE);
        
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.ALBUM_SONGS)
    public Response getAlbumSongs(@PathParam("id") Long id) {
        List<SongDTO> songDTOs = songService.findByAlbumId(id);
        List<SongResource> songResources = songResourceMapper.toResourceList(songDTOs, getBaseUrl());
        
        CollectionResource<SongResource> collection = collectionResourceMapper.createCollection(
                songResources, getBaseUrl(), ApiUriConstants.SONGS_BASE);
        
        return buildResponse(collection);
    }
}

