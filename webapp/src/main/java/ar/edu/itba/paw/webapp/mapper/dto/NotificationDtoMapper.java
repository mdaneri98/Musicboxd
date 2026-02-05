package ar.edu.itba.paw.webapp.mapper.dto;

import ar.edu.itba.paw.webapp.dto.NotificationDTO;
import ar.edu.itba.paw.webapp.dto.links.NotificationLinksDTO;
import ar.edu.itba.paw.models.Notification;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper to convert between Notification model and NotificationDTO
 */
@Component
public class NotificationDtoMapper {

    public NotificationDTO toDTO(Notification notification, UriInfo uriInfo) {
        if (notification == null) {
            return null;
        }

        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setType(notification.getType() != null ? notification.getType().name() : null);
        dto.setRecipientUserId(
                notification.getRecipientUser() != null ? notification.getRecipientUser().getId() : null);
        dto.setRecipientUsername(
                notification.getRecipientUser() != null ? notification.getRecipientUser().getUsername() : null);
        dto.setTriggerUserId(notification.getTriggerUser() != null ? notification.getTriggerUser().getId() : null);
        dto.setTriggerUsername(
                notification.getTriggerUser() != null ? notification.getTriggerUser().getUsername() : null);
        dto.setTriggerUserImageId(
                notification.getTriggerUser() != null ? notification.getTriggerUser().getImageId() : null);
        dto.setReviewId(notification.getReview() != null ? notification.getReview().getId() : null);
        dto.setReviewItemName(notification.getReview() != null ? notification.getReview().getItemName() : null);
        dto.setReviewItemImageId(notification.getReview() != null && notification.getReview().getItemImage() != null
                ? notification.getReview().getItemImage().getId()
                : null);
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setIsRead(notification.isRead());
        dto.setMessage(notification.getMessage());

        // Build HATEOAS links
        if (uriInfo != null) {
            NotificationLinksDTO links = new NotificationLinksDTO();

            links.setSelf(uriInfo.getBaseUriBuilder()
                    .path("notifications").path(String.valueOf(notification.getId())).build());

            if (notification.getRecipientUser() != null) {
                links.setRecipientUser(uriInfo.getBaseUriBuilder()
                        .path("users").path(String.valueOf(notification.getRecipientUser().getId())).build());
            }

            if (notification.getTriggerUser() != null) {
                links.setTriggerUser(uriInfo.getBaseUriBuilder()
                        .path("users").path(String.valueOf(notification.getTriggerUser().getId())).build());
            }

            if (notification.getReview() != null) {
                links.setReview(uriInfo.getBaseUriBuilder()
                        .path("reviews").path(String.valueOf(notification.getReview().getId())).build());
            }

            dto.setLinks(links);
        }

        return dto;
    }

    public List<NotificationDTO> toDTOList(List<Notification> notifications, UriInfo uriInfo) {
        if (notifications == null) {
            return null;
        }

        return notifications.stream()
                .map(n -> toDTO(n, uriInfo))
                .collect(Collectors.toList());
    }

    public Notification toModel(NotificationDTO dto) {
        if (dto == null) {
            return null;
        }

        Notification notification = new Notification();
        notification.setId(dto.getId());
        if (dto.getType() != null) {
            notification.setType(Notification.NotificationType.valueOf(dto.getType()));
        }
        notification.setCreatedAt(dto.getCreatedAt());
        notification.setRead(dto.getIsRead() != null ? dto.getIsRead() : false);
        notification.setMessage(dto.getMessage());

        return notification;
    }
}
