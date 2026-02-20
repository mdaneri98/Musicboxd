package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.NotificationDTO;
import ar.edu.itba.paw.webapp.mapper.dto.NotificationDtoMapper;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;
import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(ApiUriConstants.NOTIFICATIONS_BASE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationController extends BaseController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationDtoMapper notificationDtoMapper;

    @POST
    @Consumes(CustomMediaType.NOTIFICATION)
    @Produces(CustomMediaType.NOTIFICATION)
    public Response createNotification(@Valid NotificationDTO notificationDTO) {
        Notification notification = notificationDtoMapper.toModel(notificationDTO);
        Notification createdNotification = notificationService.create(notification);
        NotificationDTO responseDTO = notificationDtoMapper.toDTO(createdNotification, uriInfo);
        return Response.created(responseDTO.getLinks().getSelf()).entity(responseDTO).build();
    }

    @GET
    @Path(ApiUriConstants.ID)
    @Produces(CustomMediaType.NOTIFICATION)
    @PreAuthorize("@securityServiceImpl.isNotificationOwner(#id, authentication)")
    public Response getNotification(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        Notification notification = notificationService.findById(id);
        NotificationDTO notificationDTO = notificationDtoMapper.toDTO(notification, uriInfo);
        return Response.ok(notificationDTO).build();
    }

    @PUT
    @Path(ApiUriConstants.ID)
    @PreAuthorize("@securityServiceImpl.isNotificationOwner(#id, authentication)")
    @Consumes(CustomMediaType.NOTIFICATION)
    @Produces(CustomMediaType.NOTIFICATION)
    public Response updateNotification(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @Valid NotificationDTO notificationDTO) {
        notificationDTO.setId(id);
        Notification notification = notificationDtoMapper.toModel(notificationDTO);
        Notification updatedNotification = notificationService.update(notification);
        NotificationDTO responseDTO = notificationDtoMapper.toDTO(updatedNotification, uriInfo);
        return Response.ok(responseDTO).build();
    }

    @DELETE
    @Path(ApiUriConstants.ID)
    @PreAuthorize("@securityServiceImpl.isNotificationOwner(#id, authentication)")
    public Response deleteNotification(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        notificationService.delete(id);
        return Response.noContent().build();
    }

    @PATCH
    @Path(ApiUriConstants.ID)
    @PreAuthorize("@securityServiceImpl.isNotificationOwner(#id, authentication)")
    @Consumes(CustomMediaType.NOTIFICATION)
    @Produces(CustomMediaType.NOTIFICATION)
    public Response markAsRead(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id,
            @Valid NotificationDTO notificationDTO) {
        if (notificationDTO.getIsRead()) {
            notificationService.markAsRead(id);
        } else {
            return Response.noContent().build();
        }
        NotificationDTO responseDTO = notificationDtoMapper.toDTO(notificationService.findById(id), uriInfo);
        return Response.ok(responseDTO).build();
    }

}
