package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.mapper.CollectionResourceMapper;
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
    private CollectionResourceMapper collectionResourceMapper;

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
                userResources, userService.countUsers(), page, size, getBaseUrl(), ApiUriConstants.USERS_BASE);
        return buildResponse(collection);
    }

    private Response getUserBySubstring(String substring, int page, int size) {
        List<UserDTO> users = userService.findByUsernameContaining(substring, page, size);
        List<UserResource> userResources = userResourceMapper.toResourceList(users, getBaseUrl());
        Long totalCount = userService.countUsers();
        CollectionResource<UserResource> collection = collectionResourceMapper.createCollection(
                userResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.USERS_BASE);
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
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        List<ReviewDTO> reviews = reviewService.findReviewsByUserPaginated(id, page, size, loggedUserId);
        List<ReviewResource> reviewResources = reviewResourceMapper.toResourceList(reviews, getBaseUrl());
        CollectionResource<ReviewResource> collection = collectionResourceMapper.createCollection(
                reviewResources, reviewService.countReviewsByUser(id), page, size, getBaseUrl(), ApiUriConstants.USER_REVIEWS);
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.USER_FOLLOWERS)
    public Response getUserFollowers(@PathParam("id") Long id, @QueryParam("page") @DefaultValue("1") Integer page, @QueryParam("size") @DefaultValue("20") Integer size) {
        UserDTO user = userService.findUserById(id);
        List<UserDTO> followers = userService.getFollowers(id, page, size);
        List<UserResource> userResources = userResourceMapper.toResourceList(followers, getBaseUrl());
        CollectionResource<UserResource> collection = collectionResourceMapper.createCollection(
                userResources, user.getFollowersAmount().longValue(), page, size, getBaseUrl(), ApiUriConstants.USER_FOLLOWERS);
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.USER_FOLLOWINGS)
    public Response getUserFollowing(@PathParam("id") Long id, @QueryParam("page") @DefaultValue("1") Integer page, @QueryParam("size") @DefaultValue("20") Integer size) {
        UserDTO user = userService.findUserById(id);
        List<UserDTO> following = userService.getFollowings(id, page, size);
        List<UserResource> userResources = userResourceMapper.toResourceList(following, getBaseUrl());
        CollectionResource<UserResource> collection = collectionResourceMapper.createCollection(
                userResources, user.getFollowingAmount().longValue(), page, size, getBaseUrl(), ApiUriConstants.USER_FOLLOWINGS);
        return buildResponse(collection);
    }

    @POST
    @Path(ApiUriConstants.USER_FOLLOWERS)
    public Response followUser(@PathParam("id") Long id) {
        userService.createFollowing(SecurityContextUtils.getCurrentUserId(), id);
        return buildNoContentResponse();
    }

    @DELETE
    @Path(ApiUriConstants.USER_FOLLOWERS)
    public Response unfollowUser(@PathParam("id") Long id) {
        userService.undoFollowing(SecurityContextUtils.getCurrentUserId(), id);
        return buildNoContentResponse();
    }
}
