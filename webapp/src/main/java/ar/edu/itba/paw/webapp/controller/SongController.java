package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.ReviewDTO;
import ar.edu.itba.paw.webapp.dto.SongDTO;
import ar.edu.itba.paw.webapp.mapper.dto.ReviewDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.SongDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.ModSongFormMapper;
import ar.edu.itba.paw.webapp.form.ModSongForm;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;
import ar.edu.itba.paw.webapp.utils.PaginationHeadersBuilder;
import ar.edu.itba.paw.webapp.utils.SecurityContextUtils;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.services.SongService;
import ar.edu.itba.paw.services.UserService;
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
    private SongService songService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModSongFormMapper modSongFormMapper;

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
        
        List<Song> songs;
        if (search != null && !search.isEmpty()) {
            songs = songService.findByTitleContaining(search, page, size);
        } else {
            songs = songService.findPaginated(filter, page, size);
        }
        
        Long totalCount = songService.countAll();
        
        if (songs.isEmpty()) {
            return Response.noContent().build();
        }
        
        List<SongDTO> songDTOs = songDtoMapper.toDTOList(songs, uriInfo);
        
        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<SongDTO>>(songDTOs) {}
        );
        
        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, page, size, totalCount);
        return responseBuilder.build();
    }

    @POST
    @PreAuthorize("hasRole('MODERATOR')")
    @Consumes(CustomMediaType.SONG)
    @Produces(CustomMediaType.SONG)
    public Response createSong(@Valid ModSongForm modSongForm) {
        Song songInput = modSongFormMapper.toModel(modSongForm);
        Song song = songService.create(songInput);
        SongDTO songDTO = songDtoMapper.toDTO(song, uriInfo);
        return Response.created(songDTO.getSelf()).entity(songDTO).build();
    }

    @GET
    @Path(ApiUriConstants.ID)
    @Produces(CustomMediaType.SONG)
    public Response getSong(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Context Request request) {
        Song song = songService.findById(id);
        return buildResponseUsingEtag(request, () -> songDtoMapper.toDTO(song, uriInfo));
    }

    @PUT
    @Path(ApiUriConstants.ID)
    @PreAuthorize("hasRole('MODERATOR')")
    @Consumes(CustomMediaType.SONG)
    @Produces(CustomMediaType.SONG)
    public Response updateSong(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @Valid ModSongForm modSongForm) {
        Song oldSong = songService.findById(id);
        Song songToUpdate = modSongFormMapper.mergeModel(oldSong, modSongForm);
        Song updatedSong = songService.update(songToUpdate);
        SongDTO songDTO = songDtoMapper.toDTO(updatedSong, uriInfo);
        return Response.ok(songDTO).build();
    }

    @DELETE
    @Path(ApiUriConstants.ID)
    @PreAuthorize("hasRole('MODERATOR')")
    public Response deleteSong(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        songService.delete(id);
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
        
        List<Review> reviews;
        if (userId != null) {
            reviews = new ArrayList<>();
            Review review = reviewService.findSongReviewByUserId(userId, id);
            if (review != null) {
                reviews.add(review);
            }
        } else {
            reviews = reviewService.findSongReviewsPaginated(id, page, size);
        }
        
        Long totalCount = reviewService.countAll();
        
        if (reviews.isEmpty()) {
            return Response.noContent().build();
        }
        
        List<ReviewDTO> reviewDTOs = reviewDtoMapper.toDTOList(reviews, uriInfo);
        
        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<ReviewDTO>>(reviewDTOs) {}
        );
        
        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, page, size, totalCount);
        return responseBuilder.build();
    }
}
