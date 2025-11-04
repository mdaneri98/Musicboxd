package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.mapper.resource.CollectionResourceMapper;
import ar.edu.itba.paw.api.mapper.resource.UserResourceMapper;
import ar.edu.itba.paw.api.models.resources.CollectionResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.ControllerUtils;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.dtos.CreateUserDTO;
import ar.edu.itba.paw.models.dtos.UserDTO;
import ar.edu.itba.paw.api.models.resources.UserResource;
import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import ar.edu.itba.paw.api.mapper.resource.ReviewResourceMapper;
import ar.edu.itba.paw.api.models.resources.ReviewResource;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
import ar.edu.itba.paw.api.utils.SecurityContextUtils;
import ar.edu.itba.paw.models.dtos.ArtistDTO;
import ar.edu.itba.paw.api.models.resources.ArtistResource;
import ar.edu.itba.paw.api.mapper.resource.ArtistResourceMapper;
import ar.edu.itba.paw.models.dtos.AlbumDTO;
import ar.edu.itba.paw.api.models.resources.AlbumResource;
import ar.edu.itba.paw.models.dtos.SongDTO;
import ar.edu.itba.paw.api.models.resources.SongResource;
import ar.edu.itba.paw.api.form.UserProfileForm;
import ar.edu.itba.paw.api.mapper.resource.AlbumResourceMapper;
import ar.edu.itba.paw.api.mapper.resource.SongResourceMapper;
import ar.edu.itba.paw.api.form.UserForm;
import ar.edu.itba.paw.api.mapper.dto.UserFormMapper;
import ar.edu.itba.paw.api.mapper.dto.UserProfileFormMapper;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    private ImageService imageService;

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

    @GET
    public Response getAllUsers(
            @QueryParam(ControllerUtils.SEARCH_PARAM_NAME) String search,
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {

        if (search != null && !search.isEmpty()) return getUserBySubstring(search, page, size);
        
        List<UserDTO> users = userService.findPaginated(filter, page, size);
        List<UserResource> userResources = userResourceMapper.toResourceList(users, getBaseUrl());
        CollectionResource<UserResource> collection = collectionResourceMapper.createCollection(
                userResources, userService.countUsers().intValue(), page, size, getBaseUrl(), ApiUriConstants.USERS_BASE, ControllerUtils.usersCollectionLinks);
        return buildResponse(collection);
    }

    private Response getUserBySubstring(String substring, Integer page, Integer size) {
        List<UserDTO> users = userService.findByUsernameContaining(substring, page, size);
        List<UserResource> userResources = userResourceMapper.toResourceList(users, getBaseUrl());
        Integer totalCount = userService.countUsers().intValue();
        CollectionResource<UserResource> collection = collectionResourceMapper.createCollection(
                userResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.USERS_BASE, ControllerUtils.usersCollectionLinks);
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.ID)
    public Response getUser(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        UserDTO user = userService.findUserById(id);
        UserResource userResource = userResourceMapper.toResource(user, getBaseUrl());
        
        return buildResponse(userResource);
    }

    @POST
    public Response createUser(@Valid UserForm userForm) {
        CreateUserDTO createUserDTO = userFormMapper.toDTO(userForm);
        UserDTO user = userService.create(createUserDTO);
        UserResource userResource = userResourceMapper.toResource(user, getBaseUrl());

        return buildCreatedResponse(userResource);
    }

    @PUT
    @Path(ApiUriConstants.ID)
    public Response updateUser(@PathParam(ControllerUtils.ID_PARAM_NAME) Long userId, @Valid UserProfileForm userProfileForm) {
        UserDTO userDTO = userProfileFormMapper.toDTO(userProfileForm);
        userDTO.setImageId(imageService.handleImage(userProfileForm.getProfilePicture()));
        userDTO.setId(userId);
        
        UserDTO user = userService.updateUser(userDTO);
        UserResource userResource = userResourceMapper.toResource(user, getBaseUrl());

        return buildResponse(userResource);
    }

    @DELETE
    @Path(ApiUriConstants.ID)
    public Response deleteUser(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        userService.deleteById(id);
        
        return buildNoContentResponse();
    }

    @GET
    @Path(ApiUriConstants.USER_REVIEWS)
    public Response getUserReviews(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, 
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page, 
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {
        UserDTO user = userService.findUserById(id);
        List<ReviewDTO> reviews = reviewService.findReviewsByUserPaginated(id, page, size, SecurityContextUtils.getCurrentUserId());
        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviews, getBaseUrl());
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                reviewResources, user.getReviewsAmount(), page, size, getBaseUrl(), ApiUriConstants.USERS_BASE + ApiUriConstants.USER_REVIEWS, ControllerUtils.userReviewsCollectionLinks, id);
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.USER_FOLLOWERS)
    public Response getUserFollowers(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, 
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page, 
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {
        UserDTO user = userService.findUserById(id);
        List<UserDTO> followers = userService.getFollowers(id, page, size);
        List<UserResource> userResources = userResourceMapper.toResourceList(followers, getBaseUrl());
        CollectionResource<UserResource> collection = collectionResourceMapper.createCollection(
                userResources, user.getFollowersAmount(), page, size, getBaseUrl(), ApiUriConstants.USERS_BASE + ApiUriConstants.USER_FOLLOWERS, ControllerUtils.followersCollectionLinks, id);
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.USER_FOLLOWINGS)
    public Response getUserFollowing(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page, @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.FILTER_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_FILTER_STRING) FilterType filter) {
        UserDTO user = userService.findUserById(id);
        List<UserDTO> following = userService.getFollowings(id, page, size);
        List<UserResource> userResources = userResourceMapper.toResourceList(following, getBaseUrl());
        CollectionResource<UserResource> collection = collectionResourceMapper.createCollection(
                userResources, user.getFollowingAmount(), page, size, getBaseUrl(), ApiUriConstants.USERS_BASE + ApiUriConstants.USER_FOLLOWINGS, ControllerUtils.followingsCollectionLinks, id);
        return buildResponse(collection);
    }

    @POST
    @Path(ApiUriConstants.USER_FOLLOWERS)
    public Response followUser(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        userService.createFollowing(SecurityContextUtils.getCurrentUserId(), id);
        UserResource userResource = userResourceMapper.toResource(userService.findUserById(id), getBaseUrl());
        return buildCreatedResponse(userResource);
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
        List<ArtistDTO> artists = userService.getFavoriteArtists(id);
        List<ArtistResource> artistResources = artistResourceMapper.toResourceList(artists, getBaseUrl());
        CollectionResource<ArtistResource> collection = collectionResourceMapper.createCollection(
                artistResources, artists.size(), ControllerUtils.FIRST_PAGE, ControllerUtils.FAVORITE_SIZE, getBaseUrl(), ApiUriConstants.USERS_BASE + ApiUriConstants.USER_FAVORITE_ARTISTS, ControllerUtils.userFavoriteCollectionLinks, id);
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.USER_FAVORITE_ALBUMS)
    public Response getUserFavoriteAlbums(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        List<AlbumDTO> albums = userService.getFavoriteAlbums(id);
        List<AlbumResource> albumResources = albumResourceMapper.toResourceList(albums, getBaseUrl());
        CollectionResource<AlbumResource> collection = collectionResourceMapper.createCollection(
                albumResources, albums.size(), ControllerUtils.FIRST_PAGE, ControllerUtils.FAVORITE_SIZE, getBaseUrl(), ApiUriConstants.USERS_BASE + ApiUriConstants.USER_FAVORITE_ALBUMS, ControllerUtils.userFavoriteCollectionLinks, id);
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.USER_FAVORITE_SONGS)
    public Response getUserFavoriteSongs(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        List<SongDTO> songs = userService.getFavoriteSongs(id);
        List<SongResource> songResources = songResourceMapper.toResourceList(songs, getBaseUrl());
        CollectionResource<SongResource> collection = collectionResourceMapper.createCollection(
                    songResources, songs.size(), ControllerUtils.FIRST_PAGE, ControllerUtils.FAVORITE_SIZE, getBaseUrl(), ApiUriConstants.USERS_BASE + ApiUriConstants.USER_FAVORITE_SONGS, ControllerUtils.userFavoriteCollectionLinks, id);
        return buildResponse(collection);
    }

}
