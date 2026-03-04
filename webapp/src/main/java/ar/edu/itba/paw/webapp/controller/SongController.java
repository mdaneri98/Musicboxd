package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.ReviewDTO;
import ar.edu.itba.paw.webapp.dto.SongDTO;
import ar.edu.itba.paw.webapp.mapper.dto.ReviewDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.SongDtoMapper;
import ar.edu.itba.paw.webapp.form.ModSongForm;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;
import ar.edu.itba.paw.webapp.utils.PaginationHeadersBuilder;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.domain.review.SongReview;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.services.SongApplicationService;
import ar.edu.itba.paw.usecases.review.ReviewApplicationService;
import ar.edu.itba.paw.usecases.song.*;
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

@Path(ApiUriConstants.SONGS_BASE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SongController extends BaseController {

    @Autowired
    private SongApplicationService songApplicationService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewApplicationService reviewApplicationService;

    @Autowired
    private SongDtoMapper songDtoMapper;

    @Autowired
    private ReviewDtoMapper reviewDtoMapper;

    @GET
    @Produces(CustomMediaType.SONG_LIST)
    public Response getAllSongs(
            @QueryParam(ControllerUtils.SEARCH_PARAM_NAME) String search,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {

        List<ar.edu.itba.paw.views.SongView> songViews;
        if (search != null && !search.isEmpty()) {
            songViews = songApplicationService.searchViewByTitle(search, page, size);
        } else {
            songViews = songApplicationService.getAllViews(page - 1, size);
        }

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

    @POST
    @PreAuthorize("hasRole('MODERATOR')")
    @Consumes(CustomMediaType.SONG)
    @Produces(CustomMediaType.SONG)
    public Response createSong(@Valid ModSongForm modSongForm) {
        CreateSongCommand command = new CreateSongCommand(
            modSongForm.getTitle(),
            modSongForm.getDuration(),
            modSongForm.getTrackNumber(),
            modSongForm.getAlbumId(),
            null
        );

        ar.edu.itba.paw.domain.song.Song domainSong = songApplicationService.create(command);
        ar.edu.itba.paw.views.SongView songView = songApplicationService.getViewById(domainSong.getId().getValue());
        SongDTO songDTO = songDtoMapper.toDTO(songView, uriInfo);
        return Response.created(songDTO.getLinks().getSelf()).entity(songDTO).build();
    }

    @GET
    @Path(ApiUriConstants.ID)
    @Produces(CustomMediaType.SONG)
    public Response getSong(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Context Request request) {
        ar.edu.itba.paw.views.SongView songView = songApplicationService.getViewById(id);
        return buildResponseUsingEtag(request, () -> songDtoMapper.toDTO(songView, uriInfo));
    }

    @PUT
    @Path(ApiUriConstants.ID)
    @PreAuthorize("hasRole('MODERATOR')")
    @Consumes(CustomMediaType.SONG)
    @Produces(CustomMediaType.SONG)
    public Response updateSong(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @Valid ModSongForm modSongForm) {
        ar.edu.itba.paw.domain.song.Song oldDomainSong = songApplicationService.getById(id);

        UpdateSongCommand command = new UpdateSongCommand(
            id,
            modSongForm.getTitle(),
            modSongForm.getDuration(),
            modSongForm.getTrackNumber(),
            modSongForm.getAlbumId(),
            null
        );

        ar.edu.itba.paw.domain.song.Song domainSong = songApplicationService.update(command);
        ar.edu.itba.paw.views.SongView songView = songApplicationService.getViewById(domainSong.getId().getValue());
        SongDTO songDTO = songDtoMapper.toDTO(songView, uriInfo);
        return Response.ok(songDTO).build();
    }

    @DELETE
    @Path(ApiUriConstants.ID)
    @PreAuthorize("hasRole('MODERATOR')")
    public Response deleteSong(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        DeleteSongCommand command = new DeleteSongCommand(id);
        songApplicationService.delete(command);
        return Response.noContent().build();
    }

    @GET
    @Path(ApiUriConstants.SONG_REVIEWS)
    @Produces(CustomMediaType.REVIEW_LIST)
    public Response getSongReviews(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.USER_ID_PARAM_NAME) Long userId) {

        List<ReviewDTO> reviewDTOs;
        Long totalCount;

        if (userId != null) {
            List<SongReview> domainReviews = new ArrayList<>();
            reviewApplicationService.getReviewByUserAndSong(userId, id).ifPresent(domainReviews::add);
            reviewDTOs = reviewDtoMapper.toDTOList(new ArrayList<>(domainReviews), uriInfo);
            totalCount = reviewApplicationService.countAllReviews();
        } else {
            List<SongReview> domainReviews = reviewApplicationService.getReviewsBySongId(id, page, size);
            reviewDTOs = reviewDtoMapper.toDTOList(new ArrayList<>(domainReviews), uriInfo);
            totalCount = reviewApplicationService.countAllReviews();
        }

        if (reviewDTOs.isEmpty()) {
            return Response.noContent().build();
        }

        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<ReviewDTO>>(reviewDTOs) {
                });

        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, page, size, totalCount);
        return responseBuilder.build();
    }
}
