package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.mapper.UserResourceMapper;
import ar.edu.itba.paw.api.models.UserResource;
import ar.edu.itba.paw.api.utils.JwtUtils;
import ar.edu.itba.paw.models.dtos.LoginRequestDTO;
import ar.edu.itba.paw.models.dtos.LoginResponseDTO;
import ar.edu.itba.paw.models.dtos.UserDTO;
import ar.edu.itba.paw.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController extends BaseController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserResourceMapper userResourceMapper;

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequestDTO loginRequest) {
        LoginResponseDTO loginResponse = authService.login(loginRequest);
        return Response.ok(loginResponse).build();
    }

    @POST
    @Path("/refresh")
    public Response refresh(@Context HttpServletRequest request) {
        String refreshToken = JwtUtils.extractTokenFromRequest(request);
        LoginResponseDTO refreshResponse = authService.refresh(refreshToken);
        
        return Response.ok(refreshResponse).build();
    }

    @POST
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request) {
        String refreshToken = JwtUtils.extractTokenFromRequest(request);
        authService.logout(refreshToken);
        
        return Response.ok("{\"message\":\"Logged out successfully\"}").build();
    }

    @GET
    @Path("/me")
    public Response getCurrentUser(@Context HttpServletRequest request) {
        String accessToken = JwtUtils.extractTokenFromRequest(request);
        UserDTO userDTO = authService.getCurrentUser(accessToken);
        UserResource userResource = userResourceMapper.toResource(userDTO, getBaseUrl());
        
        return Response.ok(userResource).build();
    }
}
