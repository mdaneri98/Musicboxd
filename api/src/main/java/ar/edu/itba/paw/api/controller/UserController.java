package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.mapper.CollectionResourceMapper;
import ar.edu.itba.paw.api.models.links.managers.CollectionLinkManager;
import ar.edu.itba.paw.api.mapper.UserResourceMapper;
import ar.edu.itba.paw.api.models.resources.CollectionResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.dtos.CreateUserDTO;
import ar.edu.itba.paw.models.dtos.UserDTO;
import ar.edu.itba.paw.api.models.resources.UserResource;
import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import ar.edu.itba.paw.api.mapper.ReviewResourceMapper;
import ar.edu.itba.paw.api.models.resources.ReviewResource;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
import ar.edu.itba.paw.api.utils.SecurityContextUtils;
import ar.edu.itba.paw.models.dtos.ArtistDTO;
import ar.edu.itba.paw.api.models.resources.ArtistResource;
import ar.edu.itba.paw.api.mapper.ArtistResourceMapper;
import ar.edu.itba.paw.models.dtos.AlbumDTO;
import ar.edu.itba.paw.api.models.resources.AlbumResource;
import ar.edu.itba.paw.models.dtos.SongDTO;
import ar.edu.itba.paw.api.models.resources.SongResource;
import ar.edu.itba.paw.api.mapper.AlbumResourceMapper;
import ar.edu.itba.paw.api.mapper.SongResourceMapper;

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

    private CollectionLinkManager usersCollectionLinks = new CollectionLinkManager(true, false, false, true, true);
    private CollectionLinkManager followingsCollectionLinks = new CollectionLinkManager(false, false, false, false, true);
    private CollectionLinkManager followersCollectionLinks = new CollectionLinkManager(true, true, false, false, true);
    private CollectionLinkManager reviewsCollectionLinks = followingsCollectionLinks;
    private CollectionLinkManager favoriteArtistsCollectionLinks = new CollectionLinkManager(false, false, false, false, false);
    private CollectionLinkManager favoriteAlbumsCollectionLinks = new CollectionLinkManager(false, false, false, false, false);
    private CollectionLinkManager favoriteSongsCollectionLinks = new CollectionLinkManager(false, false, false, false, false);

    @GET
    public Response getAllUsers(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("search") String search,
            @QueryParam("filter") @DefaultValue("FIRST") FilterType filter) {

        if (search != null && !search.isEmpty()) return getUserBySubstring(search, page, size);
        
        List<UserDTO> users = userService.findPaginated(filter, page, size);
        List<UserResource> userResources = userResourceMapper.toResourceList(users, getBaseUrl());
        CollectionResource<UserResource> collection = collectionResourceMapper.createCollection(
                userResources, userService.countUsers(), page, size, getBaseUrl(), ApiUriConstants.USERS_BASE, usersCollectionLinks, null);
        return buildResponse(collection);
    }

    private Response getUserBySubstring(String substring, int page, int size) {
        List<UserDTO> users = userService.findByUsernameContaining(substring, page, size);
        List<UserResource> userResources = userResourceMapper.toResourceList(users, getBaseUrl());
        Long totalCount = userService.countUsers();
        CollectionResource<UserResource> collection = collectionResourceMapper.createCollection(
                userResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.USERS_BASE, usersCollectionLinks, null);
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.ID)
    public Response getUser(@PathParam("id") Long id) {
        UserDTO user = userService.findUserById(id);
        UserResource userResource = userResourceMapper.toResource(user, getBaseUrl());
        
        return buildResponse(userResource);
    }

    @POST
    public Response createUser(@Valid CreateUserDTO createUserDTO) {
        UserDTO user = userService.create(createUserDTO);
        UserResource userResource = userResourceMapper.toResource(user, getBaseUrl());

        return buildCreatedResponse(userResource);
    }

    @PUT
    @Path(ApiUriConstants.ID)
    public Response updateUser(@PathParam("id") Long userId, @Valid UserDTO userDTO) {
        userDTO.setId(userId);
        UserDTO user = userService.updateUser(userDTO);
        UserResource userResource = userResourceMapper.toResource(user, getBaseUrl());

        return buildResponse(userResource);
    }

    @DELETE
    @Path(ApiUriConstants.ID)
    public Response deleteUser(@PathParam("id") Long id) {
        userService.deleteById(id);
        
        return buildNoContentResponse();
    }

    @GET
    @Path(ApiUriConstants.USER_REVIEWS)
    public Response getUserReviews(@PathParam("id") Long id, @QueryParam("page") @DefaultValue("1") Integer page, @QueryParam("size") @DefaultValue("20") Integer size) {
        UserDTO user = userService.findUserById(id);
        List<ReviewDTO> reviews = reviewService.findReviewsByUserPaginated(id, page, size, SecurityContextUtils.getCurrentUserId());
        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviews, getBaseUrl());
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                reviewResources, user.getReviewsAmount().longValue(), page, size, getBaseUrl(), ApiUriConstants.USERS_BASE + ApiUriConstants.USER_REVIEWS, reviewsCollectionLinks, id);
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.USER_FOLLOWERS)
    public Response getUserFollowers(@PathParam("id") Long id, @QueryParam("page") @DefaultValue("1") Integer page, @QueryParam("size") @DefaultValue("20") Integer size) {
        UserDTO user = userService.findUserById(id);
        List<UserDTO> followers = userService.getFollowers(id, page, size);
        List<UserResource> userResources = userResourceMapper.toResourceList(followers, getBaseUrl());
        CollectionResource<UserResource> collection = collectionResourceMapper.createCollection(
                userResources, user.getFollowersAmount().longValue(), page, size, getBaseUrl(), ApiUriConstants.USERS_BASE + ApiUriConstants.USER_FOLLOWERS, followersCollectionLinks, id);
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.USER_FOLLOWINGS)
    public Response getUserFollowing(@PathParam("id") Long id, @QueryParam("page") @DefaultValue("1") Integer page, @QueryParam("size") @DefaultValue("20") Integer size) {
        UserDTO user = userService.findUserById(id);
        List<UserDTO> following = userService.getFollowings(id, page, size);
        List<UserResource> userResources = userResourceMapper.toResourceList(following, getBaseUrl());
        CollectionResource<UserResource> collection = collectionResourceMapper.createCollection(
                userResources, user.getFollowingAmount().longValue(), page, size, getBaseUrl(), ApiUriConstants.USERS_BASE + ApiUriConstants.USER_FOLLOWINGS, followingsCollectionLinks, id);
        return buildResponse(collection);
    }

    @POST
    @Path(ApiUriConstants.USER_FOLLOWERS)
    public Response followUser(@PathParam("id") Long id) {
        userService.createFollowing(SecurityContextUtils.getCurrentUserId(), id);
        UserResource userResource = userResourceMapper.toResource(userService.findUserById(id), getBaseUrl());
        return buildCreatedResponse(userResource);
    }

    @DELETE
    @Path(ApiUriConstants.USER_FOLLOWERS)
    public Response unfollowUser(@PathParam("id") Long id) {
        userService.undoFollowing(SecurityContextUtils.getCurrentUserId(), id);
        return buildNoContentResponse();
    }

    @GET
    @Path(ApiUriConstants.USER_FAVORITE_ARTISTS)
    public Response getUserFavoriteArtists(@PathParam("id") Long id) {
        List<ArtistDTO> artists = userService.getFavoriteArtists(id);
        List<ArtistResource> artistResources = artistResourceMapper.toResourceList(artists, getBaseUrl());
        CollectionResource<ArtistResource> collection = collectionResourceMapper.createCollection(
                artistResources, 5L, 1, 5, getBaseUrl(), ApiUriConstants.USERS_BASE + ApiUriConstants.USER_FAVORITE_ARTISTS, favoriteArtistsCollectionLinks, id);
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.USER_FAVORITE_ALBUMS)
    public Response getUserFavoriteAlbums(@PathParam("id") Long id) {
        List<AlbumDTO> albums = userService.getFavoriteAlbums(id);
        List<AlbumResource> albumResources = albumResourceMapper.toResourceList(albums, getBaseUrl());
        CollectionResource<AlbumResource> collection = collectionResourceMapper.createCollection(
                albumResources, 5L, 1, 5, getBaseUrl(), ApiUriConstants.USERS_BASE + ApiUriConstants.USER_FAVORITE_ALBUMS, favoriteAlbumsCollectionLinks, id);
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.USER_FAVORITE_SONGS)
    public Response getUserFavoriteSongs(@PathParam("id") Long id) {
        List<SongDTO> songs = userService.getFavoriteSongs(id);
        List<SongResource> songResources = songResourceMapper.toResourceList(songs, getBaseUrl());
        CollectionResource<SongResource> collection = collectionResourceMapper.createCollection(
                songResources, 5L, 1, 5, getBaseUrl(), ApiUriConstants.USERS_BASE + ApiUriConstants.USER_FAVORITE_SONGS, favoriteSongsCollectionLinks, id);
        return buildResponse(collection);
    }

}
