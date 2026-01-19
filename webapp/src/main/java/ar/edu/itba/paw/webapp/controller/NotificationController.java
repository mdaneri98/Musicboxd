package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.NotificationDTO;
import ar.edu.itba.paw.webapp.mapper.dto.NotificationDtoMapper;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;
import ar.edu.itba.paw.webapp.utils.PaginationHeadersBuilder;
import ar.edu.itba.paw.webapp.utils.SecurityContextUtils;
import ar.edu.itba.paw.models.StatusType;
import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path(ApiUriConstants.NOTIFICATIONS_BASE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationController extends BaseController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationDtoMapper notificationDtoMapper;

    @GET
    @Produces(CustomMediaType.NOTIFICATION_LIST)
    public Response getNotifications(
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.STATUS_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_STATUS_STRING) String status) {

        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        StatusType statusType = StatusType.fromString(status);

        List<Notification> notifications = notificationService.getUserNotifications(loggedUserId, page, size,
                statusType);
        Long totalCount = notificationService.countByUserId(loggedUserId, statusType);

        if (notifications.isEmpty()) {
            return Response.noContent().build();
        }

        List<NotificationDTO> notificationDTOs = notificationDtoMapper.toDTOList(notifications, uriInfo);

        Response.ResponseBuilder responseBuilder = Response.ok(
                new GenericEntity<List<NotificationDTO>>(notificationDTOs) {
                });

        PaginationHeadersBuilder.addPaginationHeaders(responseBuilder, uriInfo, page, size, totalCount);
        return responseBuilder.build();
    }

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
    public Response getNotification(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        Notification notification = notificationService.findById(id);
        NotificationDTO notificationDTO = notificationDtoMapper.toDTO(notification, uriInfo);
        return Response.ok(notificationDTO).build();
    }

    @PUT
    @Path(ApiUriConstants.ID)
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
    public Response deleteNotification(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        notificationService.delete(id);
        return Response.noContent().build();
    }

    @PATCH
    @Path(ApiUriConstants.ID)
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

    @PATCH
    @Consumes(CustomMediaType.NOTIFICATION)
    public Response markAllAsRead(@Valid NotificationDTO notificationDTO) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        if (notificationDTO.getIsRead()) {
            notificationService.markAllAsRead(loggedUserId);
        }
        return Response.noContent().build();
    }
}
