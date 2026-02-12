package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.*;
import ar.edu.itba.paw.webapp.form.UserForm;
import ar.edu.itba.paw.webapp.form.UserProfileForm;
import ar.edu.itba.paw.webapp.mapper.dto.AlbumDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.ArtistDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.ReviewDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.SongDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.UserDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.UserFormMapper;
import ar.edu.itba.paw.webapp.mapper.dto.UserProfileFormMapper;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;
import ar.edu.itba.paw.webapp.utils.PaginationHeadersBuilder;
import ar.edu.itba.paw.webapp.utils.SecurityContextUtils;
import ar.edu.itba.paw.models.VerificationType;
import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.ws.rs.ForbiddenException;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path(ApiUriConstants.USERS_BASE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserFormMapper userFormMapper;

    @Autowired
    private UserProfileFormMapper userProfileFormMapper;

    @Autowired
    private UserDtoMapper userDtoMapper;

    @Autowired
    private ReviewDtoMapper reviewDtoMapper;

    @Autowired
    private ArtistDtoMapper artistDtoMapper;

    @Autowired
    private AlbumDtoMapper albumDtoMapper;

    @Autowired
    private SongDtoMapper songDtoMapper;

    @GET
    @Produces(CustomMediaType.USER_LIST)
    public Response getAllUsers(
            @QueryParam(ControllerUtils.SEARCH_PARAM_NAME) String search,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {
        
        List<User> users;
        if (search != null && !search.isEmpty()) {
            users = userService.findByUsernameContaining(search, page, size);
        } else {
            users = userService.findPaginated(filter, page, size);
        }
        
        Long totalCount = userService.countUsers();
        
        if (users.isEmpty()) {
            return Response.noContent().build();
        }
        
        List<UserDTO> userDTOs = userDtoMapper.toDTOList(users, uriInfo);
        
        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<UserDTO>>(userDTOs) {}
        );
        
        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, page, size, totalCount);
        return responseBuilder.build();
    }

    @GET
    @Path(ApiUriConstants.ID)
    @Produces(CustomMediaType.USER)
    public Response getUser(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Context Request request) {
        User user = userService.findUserById(id);
        return buildResponseUsingEtag(request, () -> userDtoMapper.toDTO(user, uriInfo));
    }

    // ==================== User Registration ====================

    @POST
    @Consumes(CustomMediaType.USER)
    @Produces(CustomMediaType.USER)
    public Response createUser(@Valid UserForm userForm) {
        CreateUserDTO createUserDTO = userFormMapper.toDTO(userForm);
        User user = userService.create(createUserDTO.getUsername(), createUserDTO.getEmail(), createUserDTO.getPassword());
        UserDTO userDTO = userDtoMapper.toDTO(user, uriInfo);
        return Response.created(userDTO.getSelf()).entity(userDTO).build();
    }

    // ==================== Password Reset Operations ====================

    /**
     * Request password reset (forgot password).
     * Sends email with reset code.
     */
    @POST
    @Consumes(CustomMediaType.USER_PASSWORD)
    public Response requestPasswordReset(@Valid ForgotPasswordRequestDTO request) {
        User user = userService.findByEmail(request.getEmail());
        userService.createVerification(VerificationType.VERIFY_FORGOT_PASSWORD, user);
        return Response.noContent().build();
    }

    /**
     * Reset password with verification code.
     */
    @PATCH
    @Path(ApiUriConstants.ID)
    @Consumes(CustomMediaType.USER_PASSWORD)
    public Response updatePassword(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
                                   @Valid ResetPasswordRequestDTO request) {
        userService.verify(VerificationType.VERIFY_FORGOT_PASSWORD, request.getCode());
        userService.changePassword(id, request.getPassword());
        return Response.noContent().build();
    }

    // ==================== Email Verification Operations ====================

    /**
     * Verify email with verification code.
     */
    @PATCH
    @Path(ApiUriConstants.ID)
    @Consumes(CustomMediaType.USER_VERIFICATION)
    public Response verifyEmail(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
                                @Valid EmailVerificationRequestDTO request) {
        userService.verify(VerificationType.VERIFY_EMAIL, request.getCode());
        return Response.noContent().build();
    }

    /**
     * Resend verification email.
     */
    @POST
    @Consumes(CustomMediaType.USER_VERIFICATION)
    public Response resendVerificationEmail(@Valid ResendVerificationRequestDTO request) {
        User user = userService.findByEmail(request.getEmail());
        userService.createVerification(VerificationType.VERIFY_EMAIL, user);
        return Response.noContent().build();
    }

    // ==================== User CRUD Operations ====================

    @PUT
    @Path(ApiUriConstants.ID)
    @Consumes(CustomMediaType.USER)
    @Produces(CustomMediaType.USER)
    public Response updateUser(@PathParam(ControllerUtils.ID_PARAM_NAME) Long userId, @Valid UserProfileForm userProfileForm) {
        User userUpdate = userProfileFormMapper.toModel(userProfileForm);
        userUpdate.setId(userId);
        
        User user = userService.updateUser(userUpdate);
        UserDTO userDTO = userDtoMapper.toDTO(user, uriInfo);
        return Response.ok(userDTO).build();
    }

    @DELETE
    @Path(ApiUriConstants.ID)
    public Response deleteUser(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        userService.deleteById(id);
        return Response.noContent().build();
    }

    @GET
    @Path(ApiUriConstants.USER_REVIEWS)
    @Produces(CustomMediaType.REVIEW_LIST)
    public Response getUserReviews(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id, 
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page, 
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {
        
        List<Review> reviews = reviewService.findReviewsByUserPaginated(id, page, size);
        Long totalCount = reviewService.countReviewsByUser(id);
        
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

    @GET
    @Path(ApiUriConstants.USER_FOLLOWERS)
    @Produces(CustomMediaType.USER_LIST)
    public Response getUserFollowers(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id, 
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page, 
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {
        
        User user = userService.findUserById(id);
        List<User> followers = userService.getFollowers(id, page, size);
        Integer totalCount = user.getFollowersAmount();
        
        if (followers.isEmpty()) {
            return Response.noContent().build();
        }
        
        List<UserDTO> followerDTOs = userDtoMapper.toDTOList(followers, uriInfo);
        
        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<UserDTO>>(followerDTOs) {}
        );
        
        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, page, size, totalCount.longValue());
        return responseBuilder.build();
    }

    @GET
    @Path(ApiUriConstants.USER_FOLLOWINGS)
    @Produces(CustomMediaType.USER_LIST)
    public Response getUserFollowing(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {
        
        User user = userService.findUserById(id);
        List<User> following = userService.getFollowings(id, page, size);
        Integer totalCount = user.getFollowingAmount();
        
        if (following.isEmpty()) {
            return Response.noContent().build();
        }
        
        List<UserDTO> followingDTOs = userDtoMapper.toDTOList(following, uriInfo);
        
        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<UserDTO>>(followingDTOs) {}
        );
        
        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, page, size, totalCount.longValue());
        return responseBuilder.build();
    }

    @PUT
    @Path(ApiUriConstants.USER_FOLLOWING_DETAIL)
    public Response followUser(@PathParam(ControllerUtils.ID_PARAM_NAME) Long userId,
                               @PathParam(ControllerUtils.TARGET_USER_ID_PARAM_NAME) Long targetUserId) {
        userService.createFollowing(userId, targetUserId);
        return Response.noContent().build();
    }

    @DELETE
    @Path(ApiUriConstants.USER_FOLLOWING_DETAIL)
    public Response unfollowUser(@PathParam(ControllerUtils.ID_PARAM_NAME) Long userId,
                                 @PathParam(ControllerUtils.TARGET_USER_ID_PARAM_NAME) Long targetUserId) {
        userService.undoFollowing(userId, targetUserId);
        return Response.noContent().build();
    }

    @GET
    @Path(ApiUriConstants.USER_FAVORITE_ARTISTS)
    @Produces(CustomMediaType.ARTIST_LIST)
    public Response getUserFavoriteArtists(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        List<Artist> artists = userService.getFavoriteArtists(id);
        
        if (artists.isEmpty()) {
            return Response.noContent().build();
        }
        
        List<ArtistDTO> artistDTOs = artistDtoMapper.toDTOList(artists, uriInfo);
        
        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<ArtistDTO>>(artistDTOs) {}
        );
        
        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, 
                ControllerUtils.FIRST_PAGE, ControllerUtils.FAVORITE_SIZE, (long) artists.size());
        return responseBuilder.build();
    }

    @GET
    @Path(ApiUriConstants.USER_FAVORITE_ALBUMS)
    @Produces(CustomMediaType.ALBUM_LIST)
    public Response getUserFavoriteAlbums(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        List<Album> albums = userService.getFavoriteAlbums(id);
        
        if (albums.isEmpty()) {
            return Response.noContent().build();
        }
        
        List<AlbumDTO> albumDTOs = albumDtoMapper.toDTOList(albums, uriInfo);
        
        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<AlbumDTO>>(albumDTOs) {}
        );
        
        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, 
                ControllerUtils.FIRST_PAGE, ControllerUtils.FAVORITE_SIZE, (long) albums.size());
        return responseBuilder.build();
    }

    @GET
    @Path(ApiUriConstants.USER_FAVORITE_SONGS)
    @Produces(CustomMediaType.SONG_LIST)
    public Response getUserFavoriteSongs(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        List<Song> songs = userService.getFavoriteSongs(id);
        
        if (songs.isEmpty()) {
            return Response.noContent().build();
        }
        
        List<SongDTO> songDTOs = songDtoMapper.toDTOList(songs, uriInfo);
        
        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<SongDTO>>(songDTOs) {}
        );
        
        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, 
                ControllerUtils.FIRST_PAGE, ControllerUtils.FAVORITE_SIZE, (long) songs.size());
        return responseBuilder.build();
    }

    @PUT
    @Path(ApiUriConstants.USER_FAVORITE_ARTIST_DETAIL)
    public Response addFavoriteArtist(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long userId,
            @PathParam(ControllerUtils.ARTIST_ID_PARAM_NAME) Long artistId) {
        userService.addFavoriteArtist(userId, artistId);
        return Response.noContent().build();
    }

    @DELETE
    @Path(ApiUriConstants.USER_FAVORITE_ARTIST_DETAIL)
    public Response removeFavoriteArtist(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long userId,
            @PathParam(ControllerUtils.ARTIST_ID_PARAM_NAME) Long artistId) {
        userService.removeFavoriteArtist(userId, artistId);
        return Response.noContent().build();
    }

    @PUT
    @Path(ApiUriConstants.USER_FAVORITE_ALBUM_DETAIL)
    public Response addFavoriteAlbum(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long userId,
            @PathParam(ControllerUtils.ALBUM_ID_PARAM_NAME) Long albumId) {
        userService.addFavoriteAlbum(userId, albumId);
        return Response.noContent().build();
    }

    @DELETE
    @Path(ApiUriConstants.USER_FAVORITE_ALBUM_DETAIL)
    public Response removeFavoriteAlbum(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long userId,
            @PathParam(ControllerUtils.ALBUM_ID_PARAM_NAME) Long albumId) {
        userService.removeFavoriteAlbum(userId, albumId);
        return Response.noContent().build();
    }

    @PUT
    @Path(ApiUriConstants.USER_FAVORITE_SONG_DETAIL)
    public Response addFavoriteSong(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long userId,
            @PathParam(ControllerUtils.SONG_ID_PARAM_NAME) Long songId) {
        userService.addFavoriteSong(userId, songId);
        return Response.noContent().build();
    }

    @DELETE
    @Path(ApiUriConstants.USER_FAVORITE_SONG_DETAIL)
    public Response removeFavoriteSong(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long userId,
            @PathParam(ControllerUtils.SONG_ID_PARAM_NAME) Long songId) {
        userService.removeFavoriteSong(userId, songId);
        return Response.noContent().build();
    }

    @PATCH  
    @Path(ApiUriConstants.ID)
    @Consumes(CustomMediaType.USER)
    @Produces(CustomMediaType.USER)
    public Response updateUserConfig(@PathParam(ControllerUtils.ID_PARAM_NAME) Long userId, @Valid UserDTO userDTO) {
        User user = userService.findUserById(userId);
        UserDtoMapper.mergeConfigToModel(user, userDTO);
        User userUpdated = userService.updateUser(user);
        UserDTO updatedUserDTO = userDtoMapper.toDTO(userUpdated, uriInfo);
        return Response.ok(updatedUserDTO).build();
    }
}
