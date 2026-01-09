package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.AlbumDTO;
import ar.edu.itba.paw.webapp.dto.ArtistDTO;
import ar.edu.itba.paw.webapp.dto.CreateUserDTO;
import ar.edu.itba.paw.webapp.dto.ReviewDTO;
import ar.edu.itba.paw.webapp.dto.SongDTO;
import ar.edu.itba.paw.webapp.dto.UserDTO;
import ar.edu.itba.paw.webapp.form.UserForm;
import ar.edu.itba.paw.webapp.form.UserProfileForm;
import ar.edu.itba.paw.webapp.mapper.dto.AlbumDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.ArtistDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.ReviewDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.SongDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.UserDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.UserFormMapper;
import ar.edu.itba.paw.webapp.mapper.dto.UserProfileFormMapper;
import ar.edu.itba.paw.webapp.mapper.resource.AlbumResourceMapper;
import ar.edu.itba.paw.webapp.mapper.resource.ArtistResourceMapper;
import ar.edu.itba.paw.webapp.mapper.resource.CollectionResourceMapper;
import ar.edu.itba.paw.webapp.mapper.resource.ReviewResourceMapper;
import ar.edu.itba.paw.webapp.mapper.resource.SongResourceMapper;
import ar.edu.itba.paw.webapp.mapper.resource.UserResourceMapper;
import ar.edu.itba.paw.webapp.models.resources.AlbumResource;
import ar.edu.itba.paw.webapp.models.resources.ArtistResource;
import ar.edu.itba.paw.webapp.models.resources.CollectionResource;
import ar.edu.itba.paw.webapp.models.resources.ReviewResource;
import ar.edu.itba.paw.webapp.models.resources.SongResource;
import ar.edu.itba.paw.webapp.models.resources.UserResource;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import ar.edu.itba.paw.webapp.utils.SecurityContextUtils;
import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.ForbiddenException;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Path(ApiUriConstants.USERS_BASE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserResourceMapper userResourceMapper;

    @Autowired
    private ReviewResourceMapper reviewResourceMapper;

    @Autowired
    private ArtistResourceMapper artistResourceMapper;

    @Autowired
    private AlbumResourceMapper albumResourceMapper;

    @Autowired
    private SongResourceMapper songResourceMapper;

    @Autowired
    private CollectionResourceMapper collectionResourceMapper;

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
    public Response getAllUsers(
            @QueryParam(ControllerUtils.SEARCH_PARAM_NAME) String search,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {
        List<User> users = new ArrayList<>();
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        if (search != null && !search.isEmpty()) users = userService.findByUsernameContaining(search, page, size);
        else users = userService.findPaginated(filter, page, size, loggedUserId);
        List<UserDTO> userDTOs = userDtoMapper.toDTOList(users);
        List<UserResource> userResources = userResourceMapper.toResourceList(userDTOs, getBaseUrl());
        CollectionResource<UserResource> collection = collectionResourceMapper.createCollection(
                userResources, userService.countUsers().intValue(), page, size, getBaseUrl(), ApiUriConstants.USERS_BASE, ControllerUtils.usersCollectionLinks);
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.ID)
    public Response getUser(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Context Request request) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        User user = userService.findUserById(id, loggedUserId);
        return buildResponseUsingEtag(request, () -> {
            UserDTO userDTO = userDtoMapper.toDTO(user);
            return userResourceMapper.toResource(userDTO, getBaseUrl());
        });
    }

    @POST
    public Response createUser(@Valid UserForm userForm) {
        CreateUserDTO createUserDTO = userFormMapper.toDTO(userForm);
        User user = userService.create(createUserDTO.getUsername(), createUserDTO.getEmail(), createUserDTO.getPassword());
        UserDTO userDTO = userDtoMapper.toDTO(user);
        UserResource userResource = userResourceMapper.toResource(userDTO, getBaseUrl());

        return buildCreatedResponse(userResource, buildResourceLocation(ApiUriConstants.USERS_BASE, user.getId()));
    }

    @PUT
    @Path(ApiUriConstants.ID)
    public Response updateUser(@PathParam(ControllerUtils.ID_PARAM_NAME) Long userId, @Valid UserProfileForm userProfileForm) {
        User userUpdate = userProfileFormMapper.toModel(userProfileForm);
        userUpdate.setId(userId);
        
        User user = userService.updateUser(userUpdate);
        UserDTO userDTO = userDtoMapper.toDTO(user);
        UserResource userResource = userResourceMapper.toResource(userDTO, getBaseUrl());

        return buildResponse(userResource);
    }

    @DELETE
    @Path(ApiUriConstants.ID)
    public Response deleteUser(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        if (!id.equals(SecurityContextUtils.getCurrentUserId())) return Response.status(Response.Status.FORBIDDEN).build();
        userService.deleteById(id);
        return buildNoContentResponse();
    }

    @GET
    @Path(ApiUriConstants.USER_REVIEWS)
    public Response getUserReviews(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, 
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page, 
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        User user = userService.findUserById(id, loggedUserId);
        List<Review> reviews = reviewService.findReviewsByUserPaginated(id, page, size, loggedUserId);
        List<ReviewDTO> reviewDTOs = reviewDtoMapper.toDTOList(reviews);
        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviewDTOs, getBaseUrl());
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                reviewResources, user.getReviewAmount(), page, size, getBaseUrl(), ApiUriConstants.USERS_BASE + ApiUriConstants.USER_REVIEWS, ControllerUtils.userReviewsCollectionLinks, id);
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.USER_FOLLOWERS)
    public Response getUserFollowers(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, 
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page, 
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        User user = userService.findUserById(id, loggedUserId);
        List<User> followers = userService.getFollowers(id, page, size);
        List<UserDTO> followerDTOs = userDtoMapper.toDTOList(followers);
        List<UserResource> userResources = userResourceMapper.toResourceList(followerDTOs, getBaseUrl());
        CollectionResource<UserResource> collection = collectionResourceMapper.createCollection(
                userResources, user.getFollowersAmount(), page, size, getBaseUrl(), ApiUriConstants.USERS_BASE + ApiUriConstants.USER_FOLLOWERS, ControllerUtils.followersCollectionLinks, id);
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.USER_FOLLOWINGS)
    public Response getUserFollowing(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page, @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        User user = userService.findUserById(id, loggedUserId);
        List<User> following = userService.getFollowings(id, page, size);
        List<UserDTO> followingDTOs = userDtoMapper.toDTOList(following);
        List<UserResource> userResources = userResourceMapper.toResourceList(followingDTOs, getBaseUrl());
        CollectionResource<UserResource> collection = collectionResourceMapper.createCollection(
                userResources, user.getFollowingAmount(), page, size, getBaseUrl(), ApiUriConstants.USERS_BASE + ApiUriConstants.USER_FOLLOWINGS, ControllerUtils.followingsCollectionLinks, id);
        return buildResponse(collection);
    }

    @POST
    @Path(ApiUriConstants.USER_FOLLOWERS)
    public Response followUser(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        userService.createFollowing(loggedUserId, id);
        User user = userService.findUserById(id, loggedUserId);
        UserDTO userDTO = userDtoMapper.toDTO(user);
        UserResource userResource = userResourceMapper.toResource(userDTO, getBaseUrl());

        return buildCreatedResponse(userResource,
                buildNestedResourceLocation(ApiUriConstants.USERS_BASE, loggedUserId, "/followings", id));
    }

    @DELETE
    @Path(ApiUriConstants.USER_FOLLOWERS)
    public Response unfollowUser(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        userService.undoFollowing(SecurityContextUtils.getCurrentUserId(), id);
        return buildNoContentResponse();
    }

    @GET
    @Path(ApiUriConstants.USER_FAVORITE_ARTISTS)
    public Response getUserFavoriteArtists(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        List<Artist> artists = userService.getFavoriteArtists(id);
        List<ArtistDTO> artistDTOs = artistDtoMapper.toDTOList(artists);
        List<ArtistResource> artistResources = artistResourceMapper.toResourceList(artistDTOs, getBaseUrl());
        CollectionResource<ArtistResource> collection = collectionResourceMapper.createCollection(
                artistResources, artists.size(), ControllerUtils.FIRST_PAGE, ControllerUtils.FAVORITE_SIZE, getBaseUrl(), ApiUriConstants.USERS_BASE + ApiUriConstants.USER_FAVORITE_ARTISTS, ControllerUtils.userFavoriteCollectionLinks, id);
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.USER_FAVORITE_ALBUMS)
    public Response getUserFavoriteAlbums(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        List<Album> albums = userService.getFavoriteAlbums(id);
        List<AlbumDTO> albumDTOs = albumDtoMapper.toDTOList(albums);
        List<AlbumResource> albumResources = albumResourceMapper.toResourceList(albumDTOs, getBaseUrl());
        CollectionResource<AlbumResource> collection = collectionResourceMapper.createCollection(
                albumResources, albums.size(), ControllerUtils.FIRST_PAGE, ControllerUtils.FAVORITE_SIZE, getBaseUrl(), ApiUriConstants.USERS_BASE + ApiUriConstants.USER_FAVORITE_ALBUMS, ControllerUtils.userFavoriteCollectionLinks, id);
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.USER_FAVORITE_SONGS)
    public Response getUserFavoriteSongs(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        List<Song> songs = userService.getFavoriteSongs(id);
        List<SongDTO> songDTOs = songDtoMapper.toDTOList(songs);
        List<SongResource> songResources = songResourceMapper.toResourceList(songDTOs, getBaseUrl());
        CollectionResource<SongResource> collection = collectionResourceMapper.createCollection(
                    songResources, songs.size(), ControllerUtils.FIRST_PAGE, ControllerUtils.FAVORITE_SIZE, getBaseUrl(), ApiUriConstants.USERS_BASE + ApiUriConstants.USER_FAVORITE_SONGS, ControllerUtils.userFavoriteCollectionLinks, id);
        return buildResponse(collection);
    }

    @PATCH  
    @Path(ApiUriConstants.ID)
    public Response updateUserConfig(@PathParam(ControllerUtils.ID_PARAM_NAME) Long userId, @Valid UserDTO userDTO) {
        if(!Objects.equals(SecurityContextUtils.getCurrentUserId(), userId))
            throw new ForbiddenException("You are not allowed to update this user config");
        User user = userService.findUserById(userId, SecurityContextUtils.getCurrentUserId());
        UserDtoMapper.mergeConfigToModel(user, userDTO);
        User userUpdated = userService.updateUser(user);
        UserDTO updatedUserDTO = userDtoMapper.toDTO(userUpdated);
        UserResource userResource = userResourceMapper.toResource(updatedUserDTO, getBaseUrl());
        return buildResponse(userResource);
    }
}
