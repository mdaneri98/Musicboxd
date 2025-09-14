package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.mapper.CollectionResourceMapper;
import ar.edu.itba.paw.api.mapper.UserResourceMapper;
import ar.edu.itba.paw.api.models.CollectionResource;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.api.models.UserResource;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.api.utils.HATEOASUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/users")
@Produces("application/json")
@Consumes("application/json")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserResourceMapper userResourceMapper;

    @Autowired
    private CollectionResourceMapper collectionResourceMapper;

    @Context
    private HttpServletRequest request;

    /**
     * GET /users - Get all users with pagination and HATEOAS links
     */
    @GET
    public Response getAllUsers(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("search") String search) {

        String baseUrl = HATEOASUtils.getBaseUrl(request);
        List<User> users = userService.findAll(page, size);

        List<UserResource> userResources = userResourceMapper.toResourceList(users, baseUrl);
        CollectionResource<UserResource> collection = collectionResourceMapper.createCollection(
                userResources, 100L, page, size, baseUrl, "/users");

        return Response.ok(collection).build();
    }

}
