package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.AlbumDTO;
import ar.edu.itba.paw.webapp.dto.ArtistDTO;
import ar.edu.itba.paw.webapp.dto.ReviewDTO;
import ar.edu.itba.paw.webapp.dto.SongDTO;
import ar.edu.itba.paw.webapp.mapper.dto.AlbumDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.ArtistDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.SongDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.ReviewDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.ModAlbumFormMapper;
import ar.edu.itba.paw.webapp.mapper.dto.ModArtistFormMapper;
import ar.edu.itba.paw.webapp.form.ModAlbumForm;
import ar.edu.itba.paw.webapp.form.ModArtistForm;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;
import ar.edu.itba.paw.webapp.utils.PaginationHeadersBuilder;
import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.services.ArtistApplicationService;
import ar.edu.itba.paw.services.AlbumApplicationService;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.services.SongApplicationService;
import ar.edu.itba.paw.usecases.artist.*;
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

@Path(ApiUriConstants.ARTISTS_BASE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArtistController extends BaseController {

    @Autowired
    private ArtistApplicationService artistApplicationService;

    @Autowired
    private AlbumApplicationService albumApplicationService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private SongApplicationService songApplicationService;

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
    @Produces(CustomMediaType.ARTIST_LIST)
    public Response getAllArtists(
            @QueryParam(ControllerUtils.SEARCH_PARAM_NAME) String search,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {

        List<ar.edu.itba.paw.domain.artist.Artist> domainArtists;
        if (search != null && !search.isEmpty()) {
            domainArtists = artistApplicationService.searchByName(search, page, size);
        } else {
            domainArtists = artistApplicationService.getAll(page - 1, size);
        }

        Long totalCount = artistApplicationService.count();

        if (domainArtists.isEmpty()) {
            return Response.noContent().build();
        }

        List<ArtistDTO> artistDTOs = artistDtoMapper.toDTOList(domainArtists, uriInfo);

        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<ArtistDTO>>(artistDTOs) {
                });

        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, page, size, totalCount);
        return responseBuilder.build();
    }

    @POST
    @PreAuthorize("hasRole('MODERATOR')")
    @Consumes(CustomMediaType.ARTIST)
    @Produces(CustomMediaType.ARTIST)
    public Response createArtist(@Valid ModArtistForm modArtistForm) {
        Artist legacyInput = modArtistFormMapper.toModel(modArtistForm);

        CreateArtistCommand command = new CreateArtistCommand(
            legacyInput.getName(),
            legacyInput.getBio(),
            legacyInput.getImage() != null ? legacyInput.getImage().getId() : null
        );

        ar.edu.itba.paw.domain.artist.Artist domainArtist = artistApplicationService.create(command);
        ArtistDTO artistDTO = artistDtoMapper.toDTO(domainArtist, uriInfo);
        return Response.created(artistDTO.getLinks().getSelf()).entity(artistDTO).build();
    }

    @GET
    @Path(ApiUriConstants.ID)
    @Produces(CustomMediaType.ARTIST)
    public Response getArtist(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Context Request request) {
        ar.edu.itba.paw.domain.artist.Artist domainArtist = artistApplicationService.getById(id);
        return buildResponseUsingEtag(request, () -> artistDtoMapper.toDTO(domainArtist, uriInfo));
    }

    @PUT
    @Path(ApiUriConstants.ID)
    @PreAuthorize("hasRole('MODERATOR')")
    @Consumes(CustomMediaType.ARTIST)
    @Produces(CustomMediaType.ARTIST)
    public Response updateArtist(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @Valid ModArtistForm modArtistForm) {
        Artist form = modArtistFormMapper.toModel(modArtistForm);

        UpdateArtistCommand command = new UpdateArtistCommand(
            id,
            form.getName(),
            form.getBio(),
            form.getImage() != null ? form.getImage().getId() : null
        );

        ar.edu.itba.paw.domain.artist.Artist domainArtist = artistApplicationService.update(command);
        ArtistDTO artistDTO = artistDtoMapper.toDTO(domainArtist, uriInfo);
        return Response.ok(artistDTO).build();
    }

    @DELETE
    @Path(ApiUriConstants.ID)
    @PreAuthorize("hasRole('MODERATOR')")
    public Response deleteArtist(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        DeleteArtistCommand command = new DeleteArtistCommand(id);
        artistApplicationService.delete(command);
        return Response.noContent().build();
    }

    @GET
    @Path(ApiUriConstants.ARTIST_REVIEWS)
    @Produces(CustomMediaType.REVIEW_LIST)
    public Response getArtistReviews(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.USER_ID_PARAM_NAME) Long userId) {

        List<Review> reviews;
        if (userId != null) {
            reviews = new ArrayList<>();
            Review review = reviewService.findArtistReviewByUserId(userId, id);
            if (review != null) {
                reviews.add(review);
            }
        } else {
            reviews = reviewService.findArtistReviewsPaginated(id, page, size);
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
    @Path(ApiUriConstants.ARTIST_ALBUMS)
    @Produces(CustomMediaType.ALBUM_LIST)
    public Response getArtistAlbums(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size) {

        List<ar.edu.itba.paw.views.AlbumView> albumViews = albumApplicationService.getViewsByArtistId(id);
        Long totalCount = albumApplicationService.count();

        if (albumViews.isEmpty()) {
            return Response.noContent().build();
        }

        List<AlbumDTO> albumDTOs = albumDtoMapper.toDTOList(albumViews, uriInfo);

        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<AlbumDTO>>(albumDTOs) {
                });

        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, page, size, totalCount);
        return responseBuilder.build();
    }

    @POST
    @Path(ApiUriConstants.ARTIST_ALBUMS)
    @PreAuthorize("hasRole('MODERATOR')")
    @Consumes(CustomMediaType.ALBUM)
    @Produces(CustomMediaType.ALBUM)
    public Response createArtistAlbum(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @Valid ModAlbumForm modAlbumForm) {
        Album albumInput = modAlbumFormMapper.toModel(modAlbumForm);

        CreateAlbumCommand command = new CreateAlbumCommand(
            albumInput.getTitle(),
            albumInput.getGenre(),
            albumInput.getReleaseDate(),
            albumInput.getImage() != null ? albumInput.getImage().getId() : null,
            id
        );

        ar.edu.itba.paw.domain.album.Album domainAlbum = albumApplicationService.create(command);
        ar.edu.itba.paw.views.AlbumView albumView = albumApplicationService.getViewById(domainAlbum.getId().getValue());
        AlbumDTO albumDTO = albumDtoMapper.toDTO(albumView, uriInfo);
        return Response.created(albumDTO.getLinks().getSelf()).entity(albumDTO).build();
    }

    @GET
    @Path(ApiUriConstants.ARTIST_SONGS)
    @Produces(CustomMediaType.SONG_LIST)
    public Response getArtistSongs(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.POPULAR_FILTER_STRING) FilterType filter) {

        List<ar.edu.itba.paw.views.SongView> songViews = songApplicationService.getViewsByArtistId(id, filter, page, size);
        Long totalCount = songApplicationService.count();

        if (songViews.isEmpty()) {
            return Response.noContent().build();
        }

        List<SongDTO> songDTOs = songDtoMapper.toDTOList(songViews, uriInfo);

        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<SongDTO>>(songDTOs) {
                });

        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, page, size, totalCount);
        return responseBuilder.build();
    }
}
