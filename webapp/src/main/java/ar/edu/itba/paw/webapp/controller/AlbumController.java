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
import ar.edu.itba.paw.services.AlbumApplicationService;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.services.SongService;
import ar.edu.itba.paw.services.mappers.LegacyAlbumMapper;
import ar.edu.itba.paw.usecases.album.*;
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
    private AlbumApplicationService albumApplicationService;

    @Autowired
    private LegacyAlbumMapper legacyAlbumMapper;

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

        List<ar.edu.itba.paw.domain.album.Album> domainAlbums;
        if (search != null && !search.isEmpty()) {
            domainAlbums = albumApplicationService.searchByTitle(search, page, size);
        } else {
            domainAlbums = albumApplicationService.getAll(page - 1, size);
        }

        Long totalCount = albumApplicationService.count();

        List<Album> albums = domainAlbums.stream()
            .map(legacyAlbumMapper::toLegacyModel)
            .toList();

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
        Album legacyInput = modAlbumFormMapper.toModel(modAlbumForm);

        CreateAlbumCommand command = new CreateAlbumCommand(
            legacyInput.getTitle(),
            legacyInput.getGenre(),
            legacyInput.getReleaseDate(),
            legacyInput.getImage() != null ? legacyInput.getImage().getId() : null,
            legacyInput.getArtist() != null ? legacyInput.getArtist().getId() : null
        );

        ar.edu.itba.paw.domain.album.Album domainAlbum = albumApplicationService.create(command);
        Album album = legacyAlbumMapper.toLegacyModel(domainAlbum);
        AlbumDTO albumDTO = albumDtoMapper.toDTO(album, uriInfo);
        return Response.created(albumDTO.getLinks().getSelf()).entity(albumDTO).build();
    }

    @GET
    @Path(ApiUriConstants.ID)
    @Produces(CustomMediaType.ALBUM)
    public Response getAlbum(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Context Request request) {
        ar.edu.itba.paw.domain.album.Album domainAlbum = albumApplicationService.getById(id);
        Album album = legacyAlbumMapper.toLegacyModel(domainAlbum);
        return buildResponseUsingEtag(request, () -> albumDtoMapper.toDTO(album, uriInfo));
    }

    @PUT
    @Path(ApiUriConstants.ID)
    @PreAuthorize("hasRole('MODERATOR')")
    @Consumes(CustomMediaType.ALBUM)
    @Produces(CustomMediaType.ALBUM)
    public Response updateAlbum(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Valid ModAlbumForm modAlbumForm) {
        ar.edu.itba.paw.domain.album.Album oldDomainAlbum = albumApplicationService.getById(id);
        Album oldAlbum = legacyAlbumMapper.toLegacyModel(oldDomainAlbum);
        Album albumToUpdate = modAlbumFormMapper.mergeModel(oldAlbum, modAlbumForm);

        UpdateAlbumCommand command = new UpdateAlbumCommand(
            id,
            albumToUpdate.getTitle(),
            albumToUpdate.getGenre(),
            albumToUpdate.getReleaseDate(),
            albumToUpdate.getImage() != null ? albumToUpdate.getImage().getId() : null,
            albumToUpdate.getArtist() != null ? albumToUpdate.getArtist().getId() : null
        );

        ar.edu.itba.paw.domain.album.Album domainAlbum = albumApplicationService.update(command);
        Album updatedAlbum = legacyAlbumMapper.toLegacyModel(domainAlbum);
        AlbumDTO albumDTO = albumDtoMapper.toDTO(updatedAlbum, uriInfo);
        return Response.ok(albumDTO).build();
    }

    @DELETE
    @Path(ApiUriConstants.ID)
    @PreAuthorize("hasRole('MODERATOR')")
    public Response deleteAlbum(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        DeleteAlbumCommand command = new DeleteAlbumCommand(id);
        albumApplicationService.delete(command);
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
