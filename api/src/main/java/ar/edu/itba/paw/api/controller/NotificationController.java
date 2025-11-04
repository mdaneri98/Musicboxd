package ar.edu.itba.paw.api.controller;

import ar.edu.itba.paw.api.mapper.resource.CollectionResourceMapper;
import ar.edu.itba.paw.api.mapper.resource.NotificationResourceMapper;
import ar.edu.itba.paw.api.utils.ControllerUtils;
import ar.edu.itba.paw.api.models.resources.CollectionResource;
import ar.edu.itba.paw.api.models.resources.NotificationResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.SecurityContextUtils;
import ar.edu.itba.paw.models.dtos.NotificationDTO;
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

    @GET
    public Response getNotifications(
            @QueryParam(ControllerUtils.PAGE_PARAM_NAME) @DefaultValue(ControllerUtils.FIRST_PAGE_STRING) Integer page,
            @QueryParam(ControllerUtils.SIZE_PARAM_NAME) @DefaultValue(ControllerUtils.DEFAULT_SIZE_STRING) Integer size) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        List<NotificationDTO> notificationDTOs = notificationService.getUserNotifications(loggedUserId, page, size);
        List<NotificationResource> notificationResources = notificationResourceMapper.toResourceList(notificationDTOs, getBaseUrl());
        Integer totalCount = notificationService.countByUserId(loggedUserId).intValue();
        
        CollectionResource<NotificationResource> collection = collectionResourceMapper.createCollection(
                notificationResources, totalCount, page, size, getBaseUrl(), ApiUriConstants.NOTIFICATIONS_BASE, ControllerUtils.notificationsCollectionLinks);
        
        return buildResponse(collection);
    }

    @POST
    public Response createNotification(@Valid NotificationDTO notificationDTO) {
        NotificationDTO responseDTO = notificationService.create(notificationDTO);
        NotificationResource notificationResource = notificationResourceMapper.toResource(responseDTO, getBaseUrl());
        return buildCreatedResponse(notificationResource);
    }


    @GET
    @Path(ApiUriConstants.ID)
    public Response getNotification(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id) {
        NotificationDTO notificationDTO = notificationService.findById(id);
        NotificationResource notificationResource = notificationResourceMapper.toResource(notificationDTO, getBaseUrl());
        return buildResponse(notificationResource);
    }

    @PUT
    @Path(ApiUriConstants.ID)
    public Response updateNotification(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, @Valid NotificationDTO notificationDTO) {
        notificationDTO.setId(id);
        NotificationDTO responseDTO = notificationService.update(notificationDTO);
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
    public Response markAsRead(@PathParam(ControllerUtils.ID_PARAM_NAME) Long id, Boolean isRead) {
        if (isRead) notificationService.markAsRead(id);
        else return buildNoContentResponse();
        NotificationDTO notificationDTO = notificationService.findById(id);
        NotificationResource notificationResource = notificationResourceMapper.toResource(notificationDTO, getBaseUrl());
        return buildResponse(notificationResource);
    }

    @PATCH
    public Response markAllAsRead(Boolean markAllAsRead) {
        Long loggedUserId = SecurityContextUtils.getCurrentUserId();
        if (markAllAsRead) notificationService.markAllAsRead(loggedUserId);
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

