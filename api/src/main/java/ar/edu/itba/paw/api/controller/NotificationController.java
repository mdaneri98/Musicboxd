package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.dto.NotificationDTO;
import ar.edu.itba.paw.api.mapper.dto.NotificationDtoMapper;
import ar.edu.itba.paw.api.mapper.resource.CollectionResourceMapper;
import ar.edu.itba.paw.api.mapper.resource.NotificationResourceMapper;
import ar.edu.itba.paw.api.models.resources.CollectionResource;
import ar.edu.itba.paw.api.models.resources.NotificationResource;
import ar.edu.itba.paw.models.StatusType;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.ControllerUtils;
import ar.edu.itba.paw.api.utils.SecurityContextUtils;
import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.ws.rs.*;
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
    private NotificationResourceMapper notificationResourceMapper;

    @Autowired
    private CollectionResourceMapper collectionResourceMapper;

    @Autowired
    private NotificationDtoMapper notificationDtoMapper;

    @GET
    public Response getNotifications(
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size,
            @QueryParam(ControllerUtils.STATUS_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_STATUS_STRING) String status) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        List<Notification> notifications = notificationService.getUserNotifications(loggedUserId, page, size, StatusType.fromString(status));
        List<NotificationDTO> notificationDTOs = notificationDtoMapper.toDTOList(notifications);
        List<NotificationResource> notificationResources = notificationResourceMapper.toResourceList(notificationDTOs, getBaseUrl());
        Integer totalCount = notificationService.countByUserId(loggedUserId, StatusType.fromString(status)).intValue();
        CollectionResource<NotificationResource> collection = collectionResourceMapper.createCollection(
                notificationResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.NOTIFICATIONS_BASE, ControllerUtils.notificationsCollectionLinks);
        return buildResponse(collection);
    }

    @POST
    public Response createNotification(@Valid NotificationDTO notificationDTO) {
        Notification notification = notificationDtoMapper.toModel(notificationDTO);
        Notification createdNotification = notificationService.create(notification);
        NotificationDTO responseDTO = notificationDtoMapper.toDTO(createdNotification);
        NotificationResource notificationResource = notificationResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildCreatedResponse(notificationResource);
    }


    @GET
    @Path(ApiUriConstants.ID)
    public Response getNotification(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        Notification notification = notificationService.findById(id);
        NotificationDTO notificationDTO = notificationDtoMapper.toDTO(notification);
        NotificationResource notificationResource = notificationResourceMapper.toResource(notificationDTO, getBaseUrl());
        return buildResponse(notificationResource);
    }

    @PUT
    @Path(ApiUriConstants.ID)
    public Response updateNotification(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Valid NotificationDTO notificationDTO) {
        notificationDTO.setId(id);
        Notification notification = notificationDtoMapper.toModel(notificationDTO);
        Notification updatedNotification = notificationService.update(notification);
        NotificationDTO responseDTO = notificationDtoMapper.toDTO(updatedNotification);
        NotificationResource notificationResource = notificationResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildResponse(notificationResource);
    }

    @DELETE
    @Path(ApiUriConstants.ID)
    public Response deleteNotification(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        notificationService.delete(id);
        return buildNoContentResponse();
    }

    @PATCH
    @Path(ApiUriConstants.ID)
    public Response markAsRead(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Valid NotificationDTO notificationDTO) {
        if (notificationDTO.getIsRead()) notificationService.markAsRead(id);
        else return buildNoContentResponse();
        NotificationDTO responseDTO = notificationDtoMapper.toDTO(notificationService.findById(id));
        NotificationResource notificationResource = notificationResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildResponse(notificationResource);
    }

    @PATCH
    public Response markAllAsRead(@Valid NotificationDTO notificationDTO) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        if (notificationDTO.getIsRead()) notificationService.markAllAsRead(loggedUserId);
        return buildNoContentResponse();
    }

    @GET
    @Path(ApiUriConstants.NOTIFICATIONS_UNREAD_COUNT)
    public Response getUnreadCount() {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        Integer unreadCount = notificationService.getUnreadCount(loggedUserId);
        return Response.ok()
                .entity("{\"unread_count\": " + unreadCount + "}")
                .build();
    }
}
