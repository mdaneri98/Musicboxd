package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.AlbumDTO;
import ar.edu.itba.paw.webapp.dto.ReviewDTO;
import ar.edu.itba.paw.webapp.dto.SongDTO;
import ar.edu.itba.paw.webapp.mapper.dto.AlbumDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.SongDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.ReviewDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.ModAlbumFormMapper;
import ar.edu.itba.paw.webapp.mapper.dto.ModSongFormMapper;
import ar.edu.itba.paw.webapp.form.ModAlbumForm;
import ar.edu.itba.paw.webapp.form.ModSongForm;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;
import ar.edu.itba.paw.webapp.utils.PaginationHeadersBuilder;
import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.services.AlbumService;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.services.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
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
    @Produces(CustomMediaType.ALBUM_LIST)
    public Response getAllAlbums(
            @QueryParam(ControllerUtils.SEARCH_PARAM_NAME) String search,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {

        List<Album> albums;
        if (search != null && !search.isEmpty()) {
            albums = albumService.findByTitleContaining(search, page, size);
        } else {
            albums = albumService.findPaginated(filter, page, size);
        }

        Long totalCount = albumService.countAll();

        if (albums.isEmpty()) {
            return Response.noContent().build();
        }

        List<AlbumDTO> albumDTOs = albumDtoMapper.toDTOList(albums, uriInfo);

        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<AlbumDTO>>(albumDTOs) {
                });

        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, page, size, totalCount);
        return responseBuilder.build();
    }

    @POST
    @PreAuthorize("hasRole('MODERATOR')")
    @Consumes(CustomMediaType.ALBUM)
    @Produces(CustomMediaType.ALBUM)
    public Response createAlbum(@Valid ModAlbumForm modAlbumForm) {
        Album albumInput = modAlbumFormMapper.toModel(modAlbumForm);
        Album album = albumService.create(albumInput);
        AlbumDTO albumDTO = albumDtoMapper.toDTO(album, uriInfo);
        return Response.created(albumDTO.getLinks().getSelf()).entity(albumDTO).build();
    }

    @GET
    @Path(ApiUriConstants.ID)
    @Produces(CustomMediaType.ALBUM)
    public Response getAlbum(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Context Request request) {
        Album album = albumService.findById(id);
        return buildResponseUsingEtag(request, () -> albumDtoMapper.toDTO(album, uriInfo));
    }

    @PUT
    @Path(ApiUriConstants.ID)
    @PreAuthorize("hasRole('MODERATOR')")
    @Consumes(CustomMediaType.ALBUM)
    @Produces(CustomMediaType.ALBUM)
    public Response updateAlbum(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Valid ModAlbumForm modAlbumForm) {
        Album oldAlbum = albumService.findById(id);
        Album albumToUpdate = modAlbumFormMapper.mergeModel(oldAlbum, modAlbumForm);
        Album updatedAlbum = albumService.update(albumToUpdate);
        AlbumDTO albumDTO = albumDtoMapper.toDTO(updatedAlbum, uriInfo);
        return Response.ok(albumDTO).build();
    }

    @DELETE
    @Path(ApiUriConstants.ID)
    @PreAuthorize("hasRole('MODERATOR')")
    public Response deleteAlbum(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        albumService.delete(id);
        return Response.noContent().build();
    }

    @GET
    @Path(ApiUriConstants.ALBUM_REVIEWS)
    @Produces(CustomMediaType.REVIEW_LIST)
    public Response getAlbumReviews(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.USER_ID_PARAM_NAME) Long userId) {

        List<Review> reviews;
        if (userId != null) {
            reviews = new ArrayList<>();
            Review review = reviewService.findAlbumReviewByUserId(userId, id);
            if (review != null) {
                reviews.add(review);
            }
        } else {
            reviews = reviewService.findAlbumReviewsPaginated(id, page, size);
        }

        Long totalCount = reviewService.countAll();

        if (reviews.isEmpty()) {
            return Response.noContent().build();
        }

        List<ReviewDTO> reviewDTOs = reviewDtoMapper.toDTOList(reviews, uriInfo);

        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<ReviewDTO>>(reviewDTOs) {
                });

        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, page, size, totalCount);
        return responseBuilder.build();
    }

    @GET
    @Path(ApiUriConstants.ALBUM_SONGS)
    @Produces(CustomMediaType.SONG_LIST)
    public Response getAlbumSongs(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size) {

        List<Song> songs = songService.findByAlbumId(id);
        Long totalCount = songService.countAll();

        if (songs.isEmpty()) {
            return Response.noContent().build();
        }

        List<SongDTO> songDTOs = songDtoMapper.toDTOList(songs, uriInfo);

        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<SongDTO>>(songDTOs) {
                });

        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, page, size, totalCount);
        return responseBuilder.build();
    }

    @POST
    @Path(ApiUriConstants.ALBUM_SONGS)
    @PreAuthorize("hasRole('MODERATOR')")
    @Consumes(CustomMediaType.SONG)
    @Produces(CustomMediaType.SONG)
    public Response createAlbumSong(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @Valid ModSongForm modSongForm) {
        Song songInput = modSongFormMapper.toModel(modSongForm, id);
        Song song = songService.create(songInput);
        SongDTO songDTO = songDtoMapper.toDTO(song, uriInfo);
        return Response.created(songDTO.getLinks().getSelf()).entity(songDTO).build();
    }
}
