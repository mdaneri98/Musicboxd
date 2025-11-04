package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.mapper.resource.UserResourceMapper;
import ar.edu.itba.paw.api.models.resources.UserResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.JwtUtils;
import ar.edu.itba.paw.models.dtos.CreateUserDTO;
import ar.edu.itba.paw.models.dtos.LoginRequestDTO;
import ar.edu.itba.paw.models.dtos.LoginResponseDTO;
import ar.edu.itba.paw.models.dtos.UserDTO;
import ar.edu.itba.paw.services.AuthService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.api.form.UserForm;
import ar.edu.itba.paw.api.mapper.dto.UserFormMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(ApiUriConstants.AUTH_BASE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController extends BaseController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserResourceMapper userResourceMapper;

    @Autowired
    private UserFormMapper userFormMapper;

    @POST
    @Path(ApiUriConstants.LOGIN)
    public Response login(@Valid LoginRequestDTO loginRequest) {
        LoginResponseDTO loginResponse = authService.login(loginRequest);
        return Response.ok(loginResponse).build();
    }

    @POST
    @Path(ApiUriConstants.REGISTER)
    public Response register(@Valid UserForm userForm) {
        CreateUserDTO createUserDTO = userFormMapper.toDTO(userForm);
        UserDTO userDTO = userService.create(createUserDTO);
        UserResource userResource = userResourceMapper.toResource(userDTO, getBaseUrl());
        
        return Response.status(Response.Status.CREATED).entity(userResource).build();
    }

    @POST
    @Path(ApiUriConstants.REFRESH)
    public Response refresh(@Context HttpServletRequest request) {
        String refreshToken = JwtUtils.extractTokenFromRequest(request);
        LoginResponseDTO refreshResponse = authService.refresh(refreshToken);
        
        return Response.ok(refreshResponse).build();
    }

    @POST
    @Path(ApiUriConstants.LOGOUT)
    public Response logout(@Context HttpServletRequest request) {
        String refreshToken = JwtUtils.extractTokenFromRequest(request);
        authService.logout(refreshToken);
        
        return Response.ok("{\"message\":\"Logged out successfully\"}").build();
    }

    @GET
    @Path(ApiUriConstants.ME)
    public Response getCurrentUser(@Context HttpServletRequest request) {
        String accessToken = JwtUtils.extractTokenFromRequest(request);
        UserDTO userDTO = authService.getCurrentUser(accessToken);
        UserResource userResource = userResourceMapper.toResource(userDTO, getBaseUrl());
        
        return Response.ok(userResource).build();
    }
}
