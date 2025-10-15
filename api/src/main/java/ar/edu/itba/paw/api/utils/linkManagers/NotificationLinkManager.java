package ar.edu.itba.paw.api.utils.linkManagers;

import ar.edu.itba.paw.api.resources.CollectionResource;
import ar.edu.itba.paw.api.resources.NotificationResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.HATEOASUtils;
import ar.edu.itba.paw.api.utils.UriBuilder;
import ar.edu.itba.paw.models.dtos.NotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationLinkManager {

    @Autowired
    private UriBuilder uriBuilder;

    public void addNotificationLinks(NotificationResource resource, String baseUrl) {
        HATEOASUtils.addCrudLinks(resource, baseUrl, ApiUriConstants.NOTIFICATIONS_BASE, resource.getData().getId());
        resource.addLink(uriBuilder.buildNotificationReadUri(baseUrl, resource.getData().getId()), "read", "Mark notification as read", "PUT");
        resource.addLink(uriBuilder.buildNotificationReadAllUri(baseUrl), "read-all", "Mark all notifications as read", "PUT");
        resource.addLink(uriBuilder.buildNotificationUnreadCountUri(baseUrl), "unread-count", "Get unread notifications count", "GET");
        addRelationshipLinks(resource, baseUrl, resource.getData());
    }

    private void addRelationshipLinks(NotificationResource resource, String baseUrl, NotificationDTO dto) {
        // Link al usuario receptor
        if (dto.getRecipientUserId() != null) {
            HATEOASUtils.addRelationshipLinks(resource, baseUrl, ApiUriConstants.USERS_BASE, 
                dto.getRecipientUserId(), "recipient-user", null);
        }

        // Link al usuario que disparó la notificación
        if (dto.getTriggerUserId() != null) {
            HATEOASUtils.addRelationshipLinks(resource, baseUrl, ApiUriConstants.USERS_BASE,
                dto.getTriggerUserId(), "trigger-user", null);
        }

        // Link a la review relacionada (si existe)
        if (dto.getReviewId() != null) {
            HATEOASUtils.addRelationshipLinks(resource, baseUrl, ApiUriConstants.REVIEWS_BASE,
                dto.getReviewId(), "review", null);
        }

        // Link a la imagen del usuario que disparó la notificación
        if (dto.getTriggerUserImageId() != null) {
            HATEOASUtils.addImageLinks(resource, baseUrl, ApiUriConstants.IMAGES_BASE, 
                dto.getTriggerUserImageId());
        }
    }
}

