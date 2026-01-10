package ar.edu.itba.paw.webapp.models.links.managers;

import ar.edu.itba.paw.webapp.models.resources.NotificationResource;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.webapp.utils.HATEOASUtils;
import ar.edu.itba.paw.webapp.utils.UriBuilder;
import ar.edu.itba.paw.webapp.dto.NotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
@Component
public class NotificationLinkManager {

    @Autowired
    private UriBuilder uriBuilder;

    public void addNotificationLinks(NotificationResource resource, String baseUrl, NotificationDTO dto) {
        HATEOASUtils.addCrudLinks(resource, baseUrl, ApiUriConstants.NOTIFICATIONS_BASE, dto.getId());
        resource.addLink(uriBuilder.buildNotificationReadUri(baseUrl, dto.getId()), ControllerUtils.RELATION_READ, "Mark notification as read", ControllerUtils.METHOD_PUT);
        resource.addLink(uriBuilder.buildNotificationReadAllUri(baseUrl), ControllerUtils.RELATION_READ_ALL, "Mark all notifications as read", ControllerUtils.METHOD_PUT);
        resource.addLink(uriBuilder.buildNotificationUnreadCountUri(baseUrl), ControllerUtils.RELATION_UNREAD_COUNT, "Get unread notifications count", ControllerUtils.METHOD_GET);
        addRelationshipLinks(resource, baseUrl, dto);
    }

    private void addRelationshipLinks(NotificationResource resource, String baseUrl, NotificationDTO dto) {
        // Link al usuario receptor
        if (dto.getRecipientUserId() != null) {
            resource.addLink(uriBuilder.buildUserUri(baseUrl, dto.getRecipientUserId()), ControllerUtils.RELATION_RECIPIENT_USER, "Recipient user", ControllerUtils.METHOD_GET);
        }

        // Link al usuario que disparó la notificación
        if (dto.getTriggerUserId() != null) {
            resource.addLink(uriBuilder.buildUserUri(baseUrl, dto.getTriggerUserId()), ControllerUtils.RELATION_TRIGGER_USER, "Trigger user", ControllerUtils.METHOD_GET);
        }

        // Link a la review relacionada (si existe)
        if (dto.getReviewId() != null) {
            resource.addLink(uriBuilder.buildReviewUri(baseUrl, dto.getReviewId()), ControllerUtils.RELATION_REVIEW, "Review", ControllerUtils.METHOD_GET);
        }

        // Link a la imagen del usuario que disparó la notificación
        if (dto.getTriggerUserImageId() != null) {
            resource.addLink(uriBuilder.buildImageUri(baseUrl, dto.getTriggerUserImageId()), ControllerUtils.RELATION_IMAGE, "Trigger user image", ControllerUtils.METHOD_GET);
        }
    }
}

