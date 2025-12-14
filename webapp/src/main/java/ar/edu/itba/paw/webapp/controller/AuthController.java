package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.CreateUserDTO;
import ar.edu.itba.paw.webapp.dto.LoginRequestDTO;
import ar.edu.itba.paw.webapp.dto.LoginResponseDTO;
import ar.edu.itba.paw.webapp.dto.UserDTO;
import ar.edu.itba.paw.webapp.form.UserForm;
import ar.edu.itba.paw.webapp.mapper.dto.UserDtoMapper;
import ar.edu.itba.paw.webapp.mapper.dto.UserFormMapper;
import ar.edu.itba.paw.webapp.mapper.resource.UserResourceMapper;
import ar.edu.itba.paw.webapp.models.resources.UserResource;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.webapp.utils.JwtUtils;
import ar.edu.itba.paw.models.AuthResult;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.AuthService;
import ar.edu.itba.paw.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserResourceMapper userResourceMapper;

    @Autowired
    private UserFormMapper userFormMapper;

    @Autowired
    private UserDtoMapper userDtoMapper;

    @POST
    @Path(ApiUriConstants.LOGIN)
    public Response login(@Valid LoginRequestDTO loginRequest) {
        AuthResult authResult = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
        UserDTO userDTO = userDtoMapper.toDTO(authResult.getUser());
        LoginResponseDTO loginResponse = new LoginResponseDTO(authResult.getAccessToken(), authResult.getRefreshToken(), userDTO);
        return Response.ok(loginResponse).build();
    }

    @POST
    @Path(ApiUriConstants.REGISTER)
    public Response register(@Valid UserForm userForm) {
        LOGGER.info("Register request received - Username: {}, Email: {}, Password: {}, RepeatPassword: {}",
            userForm.getUsername(), userForm.getEmail(),
            userForm.getPassword() != null ? "***" : "null",
            userForm.getRepeatPassword() != null ? "***" : "null");

        CreateUserDTO createUserDTO = userFormMapper.toDTO(userForm);
        User user = userService.create(createUserDTO.getUsername(), createUserDTO.getEmail(), createUserDTO.getPassword());
        UserDTO userDTO = userDtoMapper.toDTO(user);
        UserResource userResource = userResourceMapper.toResource(userDTO, getBaseUrl());

        return Response.status(Response.Status.CREATED).entity(userResource).build();
    }

    @POST
    @Path(ApiUriConstants.REFRESH)
    public Response refresh(@Context HttpServletRequest request) {
        String refreshToken = JwtUtils.extractTokenFromRequest(request);
        AuthResult authResult = authService.refresh(refreshToken);
        UserDTO userDTO = userDtoMapper.toDTO(authResult.getUser());
        LoginResponseDTO refreshResponse = new LoginResponseDTO(authResult.getAccessToken(), authResult.getRefreshToken(), userDTO);
        
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
        User user = authService.getCurrentUser(accessToken);
        UserDTO userDTO = userDtoMapper.toDTO(user);
        UserResource userResource = userResourceMapper.toResource(userDTO, getBaseUrl());

        return Response.ok(userResource).build();
    }
}
