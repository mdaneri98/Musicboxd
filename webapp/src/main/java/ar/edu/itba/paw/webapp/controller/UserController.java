package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.domain.user.User;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.usecases.user.*;
import ar.edu.itba.paw.usecases.review.ReviewApplicationService;
import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.domain.user.Email;
import ar.edu.itba.paw.domain.artist.Artist;
import ar.edu.itba.paw.domain.album.Album;
import ar.edu.itba.paw.domain.song.Song;
import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.StatusType;
import ar.edu.itba.paw.webapp.dto.*;
import ar.edu.itba.paw.webapp.form.UserForm;
import ar.edu.itba.paw.webapp.form.UserProfileForm;
import ar.edu.itba.paw.webapp.mapper.dto.*;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;
import ar.edu.itba.paw.webapp.utils.PaginationHeadersBuilder;
import ar.edu.itba.paw.models.reviews.Review;
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

@Path(ApiUriConstants.USERS_BASE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController extends BaseController {

    @Autowired
    private UserApplicationService userApplicationService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewApplicationService reviewApplicationService;

    @Autowired
    private UserFormMapper userFormMapper;

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

    @Autowired
    private NotificationDtoMapper notificationDtoMapper;

    @GET
    @Produces(CustomMediaType.USER_LIST)
    public Response getAllUsers(
            @QueryParam(ControllerUtils.SEARCH_PARAM_NAME) String search,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {

        List<User> users = userApplicationService.getAllUsers(search, page, size);
        Long totalCount = userApplicationService.countAllUsers();

        if (users.isEmpty()) {
            return Response.noContent().build();
        }

        List<UserDTO> userDTOs = userDtoMapper.toDTOList(users, uriInfo);

        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<UserDTO>>(userDTOs) {
                });

        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, page, size, totalCount);
        return responseBuilder.build();
    }

    @GET
    @Path(ApiUriConstants.ID)
    @Produces(CustomMediaType.USER)
    public Response getUser(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Context Request request) {
        User user = userApplicationService.getUser(id);
        return buildResponseUsingEtag(request, () -> userDtoMapper.toDTO(user, uriInfo));
    }

    // ==================== User Registration ====================

    @POST
    @Consumes(CustomMediaType.USER)
    @Produces(CustomMediaType.USER)
    public Response createUser(@Valid UserForm userForm) {
        CreateUserDTO createUserDTO = userFormMapper.toDTO(userForm);
        CreateUserCommand command = new CreateUserCommand(
            createUserDTO.getUsername(),
            createUserDTO.getEmail(),
            createUserDTO.getPassword()
        );
        User user = userApplicationService.createUser(command);
        UserDTO userDTO = userDtoMapper.toDTO(user, uriInfo);
        return Response.created(userDTO.getLinks().getSelf()).entity(userDTO).build();
    }

    // ==================== Password Reset Operations ====================

    /**
     * Request password reset (forgot password).
     * Sends email with reset code.
     */
    @POST
    @Consumes(CustomMediaType.USER_PASSWORD)
    public Response requestPasswordReset(@Valid ForgotPasswordRequestDTO request) {
        // TODO: Implement RequestPasswordReset use case
        // User user = getUser.execute(...);
        // requestPasswordReset.execute(user.getId());
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
        // TODO: Implement password change with verification
        // if (!userService.changePassword(id, request.getPassword(), request.getCode())) {
        //     return Response.status(Response.Status.BAD_REQUEST).build();
        // }
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
        // TODO: Implement email verification with use cases
        // userService.verify(VerificationType.VERIFY_EMAIL, request.getCode());
        return Response.noContent().build();
    }

    // ==================== User CRUD Operations ====================

    @PUT
    @Path(ApiUriConstants.ID)
    @PreAuthorize("@securityServiceImpl.isCurrentUser(#userId, authentication)")
    @Consumes(CustomMediaType.USER)
    @Produces(CustomMediaType.USER)
    public Response updateUser(@PathParam(ControllerUtils.ID_PARAM_NAME) Long userId,
            @Valid UserProfileForm userProfileForm) {
        UpdateUserProfileCommand command = new UpdateUserProfileCommand(
            userId,
            userProfileForm.getName(),
            userProfileForm.getBio(),
            userProfileForm.getImageId()
        );
        User updatedUser = userApplicationService.updateUserProfile(command);
        UserDTO userDTO = userDtoMapper.toDTO(updatedUser, uriInfo);
        return Response.ok(userDTO).build();
    }

    @DELETE
    @Path(ApiUriConstants.ID)
    @PreAuthorize("@securityServiceImpl.isCurrentUser(#id, authentication) or hasRole('MODERATOR')")
    public Response deleteUser(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        DeleteUserCommand command = new DeleteUserCommand(id);
        userApplicationService.deleteUser(command);
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

        List<ar.edu.itba.paw.domain.review.Review> domainReviews = reviewApplicationService.getReviewsByUserId(id, page, size);
        Long totalCount = reviewApplicationService.countReviewsByUserId(id);

        if (domainReviews.isEmpty()) {
            return Response.noContent().build();
        }

        List<ReviewDTO> reviewDTOs = reviewDtoMapper.toDTOList(domainReviews, uriInfo);

        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<ReviewDTO>>(reviewDTOs) {
                });

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

        List<User> followers = userApplicationService.getUserFollowers(id, page, size);
        Long totalCount = userApplicationService.countUserFollowers(id);

        if (followers.isEmpty()) {
            return Response.noContent().build();
        }

        List<UserDTO> followerDTOs = userDtoMapper.toDTOList(followers, uriInfo);

        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<UserDTO>>(followerDTOs) {
                });

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

        List<User> following = userApplicationService.getUserFollowing(id, page, size);
        Long totalCount = userApplicationService.countUserFollowing(id);

        if (following.isEmpty()) {
            return Response.noContent().build();
        }

        List<UserDTO> followingDTOs = userDtoMapper.toDTOList(following, uriInfo);

        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<UserDTO>>(followingDTOs) {
                });

        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, page, size, totalCount.longValue());
        return responseBuilder.build();
    }

    @GET
    @Path(ApiUriConstants.USER_FOLLOWING_REVIEWS)
    @Produces(CustomMediaType.REVIEW_LIST)
    @PreAuthorize("@securityServiceImpl.isCurrentUser(#id, authentication)")
    public Response getUserFollowingReviews(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size) {

        List<Review> reviews = reviewService.getReviewsFromFollowedUsersPaginated(page, size, id);
        Long totalCount = reviewService.countReviewsFromFollowedUsers(id);

        if (reviews.isEmpty()) {
            return Response.noContent().build();
        }

        List<ReviewDTO> reviewDTOs = reviewDtoMapper.toDTOListLegacy(reviews, uriInfo);

        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<ReviewDTO>>(reviewDTOs) {
                });

        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, page, size, totalCount);
        return responseBuilder.build();
    }

    @PUT
    @Path(ApiUriConstants.USER_FOLLOWING_DETAIL)
    @PreAuthorize("@securityServiceImpl.isCurrentUser(#userId, authentication)")
    public Response followUser(@PathParam(ControllerUtils.ID_PARAM_NAME) Long userId,
            @PathParam(ControllerUtils.TARGET_USER_ID_PARAM_NAME) Long targetUserId) {
        userApplicationService.followUser(userId, targetUserId);
        return Response.noContent().build();
    }

    @DELETE
    @Path(ApiUriConstants.USER_FOLLOWING_DETAIL)
    @PreAuthorize("@securityServiceImpl.isCurrentUser(#userId, authentication)")
    public Response unfollowUser(@PathParam(ControllerUtils.ID_PARAM_NAME) Long userId,
            @PathParam(ControllerUtils.TARGET_USER_ID_PARAM_NAME) Long targetUserId) {
        userApplicationService.unfollowUser(userId, targetUserId);
        return Response.noContent().build();
    }

    @GET
    @Path(ApiUriConstants.USER_RECOMMENDED)
    @Produces(CustomMediaType.USER_LIST)
    @PreAuthorize("@securityServiceImpl.isCurrentUser(#id, authentication)")
    public Response getRecommendedUsers(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size) {

        // TODO: Implement recommendation algorithm
        // For now, return empty list
        return Response.noContent().build();
    }

    @GET
    @Path(ApiUriConstants.USER_FAVORITE_ARTISTS)
    @Produces(CustomMediaType.ARTIST_LIST)
    public Response getUserFavoriteArtists(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        List<Artist> artists = userApplicationService.getUserFavoriteArtists(id, null, null);

        if (artists.isEmpty()) {
            return Response.noContent().build();
        }

        List<ArtistDTO> artistDTOs = artistDtoMapper.toDTOList(artists, uriInfo);

        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<ArtistDTO>>(artistDTOs) {
                });

        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo,
                ControllerUtils.FIRST_PAGE, ControllerUtils.FAVORITE_SIZE, (long) artists.size());
        return responseBuilder.build();
    }

    @GET
    @Path(ApiUriConstants.USER_FAVORITE_ALBUMS)
    @Produces(CustomMediaType.ALBUM_LIST)
    public Response getUserFavoriteAlbums(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        List<ar.edu.itba.paw.views.AlbumView> albumViews = userApplicationService.getUserFavoriteAlbumsView(id, null, null);

        if (albumViews.isEmpty()) {
            return Response.noContent().build();
        }

        List<AlbumDTO> albumDTOs = albumDtoMapper.toDTOList(albumViews, uriInfo);

        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<AlbumDTO>>(albumDTOs) {
                });

        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo,
                ControllerUtils.FIRST_PAGE, ControllerUtils.FAVORITE_SIZE, (long) albumViews.size());
        return responseBuilder.build();
    }

    @GET
    @Path(ApiUriConstants.USER_FAVORITE_SONGS)
    @Produces(CustomMediaType.SONG_LIST)
    public Response getUserFavoriteSongs(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        List<ar.edu.itba.paw.views.SongView> songViews = userApplicationService.getUserFavoriteSongsView(id, null, null);

        if (songViews.isEmpty()) {
            return Response.noContent().build();
        }

        List<SongDTO> songDTOs = songDtoMapper.toDTOList(songViews, uriInfo);

        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<SongDTO>>(songDTOs) {
                });

        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo,
                ControllerUtils.FIRST_PAGE, ControllerUtils.FAVORITE_SIZE, (long) songViews.size());
        return responseBuilder.build();
    }

    @PUT
    @Path(ApiUriConstants.USER_FAVORITE_ARTIST_DETAIL)
    @PreAuthorize("@securityServiceImpl.isCurrentUser(#userId, authentication)")
    public Response addFavoriteArtist(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long userId,
            @PathParam(ControllerUtils.ARTIST_ID_PARAM_NAME) Long artistId) {
        userApplicationService.addFavoriteArtist(userId, artistId);
        return Response.noContent().build();
    }

    @DELETE
    @Path(ApiUriConstants.USER_FAVORITE_ARTIST_DETAIL)
    @PreAuthorize("@securityServiceImpl.isCurrentUser(#userId, authentication)")
    public Response removeFavoriteArtist(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long userId,
            @PathParam(ControllerUtils.ARTIST_ID_PARAM_NAME) Long artistId) {
        userApplicationService.removeFavoriteArtist(userId, artistId);
        return Response.noContent().build();
    }

    @PUT
    @Path(ApiUriConstants.USER_FAVORITE_ALBUM_DETAIL)
    @PreAuthorize("@securityServiceImpl.isCurrentUser(#userId, authentication)")
    public Response addFavoriteAlbum(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long userId,
            @PathParam(ControllerUtils.ALBUM_ID_PARAM_NAME) Long albumId) {
        userApplicationService.addFavoriteAlbum(userId, albumId);
        return Response.noContent().build();
    }

    @DELETE
    @Path(ApiUriConstants.USER_FAVORITE_ALBUM_DETAIL)
    @PreAuthorize("@securityServiceImpl.isCurrentUser(#userId, authentication)")
    public Response removeFavoriteAlbum(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long userId,
            @PathParam(ControllerUtils.ALBUM_ID_PARAM_NAME) Long albumId) {
        userApplicationService.removeFavoriteAlbum(userId, albumId);
        return Response.noContent().build();
    }

    @PUT
    @Path(ApiUriConstants.USER_FAVORITE_SONG_DETAIL)
    @PreAuthorize("@securityServiceImpl.isCurrentUser(#userId, authentication)")
    public Response addFavoriteSong(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long userId,
            @PathParam(ControllerUtils.SONG_ID_PARAM_NAME) Long songId) {
        userApplicationService.addFavoriteSong(userId, songId);
        return Response.noContent().build();
    }

    @DELETE
    @Path(ApiUriConstants.USER_FAVORITE_SONG_DETAIL)
    @PreAuthorize("@securityServiceImpl.isCurrentUser(#userId, authentication)")
    public Response removeFavoriteSong(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long userId,
            @PathParam(ControllerUtils.SONG_ID_PARAM_NAME) Long songId) {
        userApplicationService.removeFavoriteSong(userId, songId);
        return Response.noContent().build();
    }

    @PATCH
    @Path(ApiUriConstants.ID)
    @PreAuthorize("@securityServiceImpl.isCurrentUser(#userId, authentication)")
    @Consumes(CustomMediaType.USER)
    @Produces(CustomMediaType.USER)
    public Response updateUserConfig(@PathParam(ControllerUtils.ID_PARAM_NAME) Long userId, @Valid UserDTO userDTO) {
        UpdateUserConfigCommand command = new UpdateUserConfigCommand(
            userId,
            userDTO.getPreferredLanguage(),
            userDTO.getPreferredTheme(),
            userDTO.getHasFollowNotificationsEnabled(),
            userDTO.getHasLikeNotificationsEnabled(),
            userDTO.getHasCommentsNotificationsEnabled(),
            userDTO.getHasReviewsNotificationsEnabled()
        );
        User updatedUser = userApplicationService.updateUserConfig(command);
        UserDTO updatedUserDTO = userDtoMapper.toDTO(updatedUser, uriInfo);
        return Response.ok(updatedUserDTO).build();
    }

    // ==================== User Likes ====================

    @GET
    @Path(ApiUriConstants.USER_LIKES)
    @PreAuthorize("@securityServiceImpl.isCurrentUser(#userId, authentication)")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserLikedReviewIds(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long userId,
            @QueryParam(ControllerUtils.REVIEW_IDS_PARAM_NAME) List<Long> reviewIds) {

        if (reviewIds == null || reviewIds.isEmpty()) {
            return Response.ok(List.of()).build();
        }

        List<Long> likedIds = reviewService.getLikedReviewIds(userId, reviewIds);
        return Response.ok(new GenericEntity<List<Long>>(likedIds) {
        }).build();
    }

    // ==================== User Notifications ====================

    @GET
    @Path(ApiUriConstants.USER_NOTIFICATIONS)
    @PreAuthorize("@securityServiceImpl.isCurrentUser(#userId, authentication)")
    @Produces(CustomMediaType.NOTIFICATION_LIST)
    public Response getUserNotifications(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long userId,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.STATUS_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_STATUS_STRING) String status) {

        StatusType statusType = StatusType.fromString(status);

        List<Notification> notifications = notificationService.getUserNotifications(userId, page, size, statusType);
        Long totalCount = notificationService.countByUserId(userId, statusType);

        if (notifications.isEmpty()) {
            return Response.noContent().build();
        }

        List<NotificationDTO> notificationDTOs = notificationDtoMapper.toDTOList(notifications, uriInfo);

        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<NotificationDTO>>(notificationDTOs) {
                });

        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, page, size, totalCount);
        return responseBuilder.build();
    }

    @PATCH
    @Path(ApiUriConstants.USER_NOTIFICATIONS)
    @PreAuthorize("@securityServiceImpl.isCurrentUser(#userId, authentication)")
    @Consumes(CustomMediaType.NOTIFICATION)
    public Response markAllNotificationsAsRead(
            @PathParam(ControllerUtils.ID_PARAM_NAME) Long userId,
            @Valid NotificationDTO notificationDTO) {
        if (notificationDTO.getIsRead()) {
            notificationService.markAllAsRead(userId);
        }
        return Response.noContent().build();
    }
}
