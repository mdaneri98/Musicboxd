package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.mapper.CollectionResourceMapper;
import ar.edu.itba.paw.api.mapper.UserResourceMapper;
import ar.edu.itba.paw.api.models.CollectionResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.dtos.CreateUserDTO;
import ar.edu.itba.paw.models.dtos.UserDTO;
import ar.edu.itba.paw.api.models.UserResource;
import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

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
    private UserResourceMapper userResourceMapper;

    @Autowired
    private CollectionResourceMapper collectionResourceMapper;

    @GET
    public Response getAllUsers(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("search") String search,
            @QueryParam("filter") @DefaultValue("FIRST") FilterType filter) {
        List<UserDTO> users = userService.findPaginated(filter, page, size);
        List<UserResource> userResources = userResourceMapper.toResourceList(users, getBaseUrl());
        CollectionResource<UserResource> collection = collectionResourceMapper.createCollection(
                userResources, userService.countUsers(), page, size, getBaseUrl(), ApiUriConstants.USERS_BASE);
        return buildResponse(collection);
    }

    @GET
    @Path(ApiUriConstants.USER_BY_ID)
    public Response getUser(@PathParam("userId") Long id) {
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
    @Path(ApiUriConstants.USER_BY_ID)
    public Response updateUser(@PathParam("userId") Long userId, @Valid UserDTO userDTO) {
        UserDTO user = userService.updateUser(userId, userDTO);
        UserResource userResource = userResourceMapper.toResource(user, getBaseUrl());

        return buildResponse(userResource);
    }

    @DELETE
    @Path(ApiUriConstants.USER_BY_ID)
    public Response deleteUser(@PathParam("userId") Long id) {
        userService.deleteById(id);
        
        return buildNoContentResponse();
    }

}
