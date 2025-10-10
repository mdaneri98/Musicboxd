package ar.edu.itba.paw.services.mappers;

import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.dtos.NotificationDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationMapper {

    public NotificationDTO toDTO(Notification notification) {
        if (notification == null) {
            return null;
        }

        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setType(notification.getType() != null ? notification.getType().name() : null);
        dto.setRecipientUserId(notification.getRecipientUser() != null ? notification.getRecipientUser().getId() : null);
        dto.setRecipientUsername(notification.getRecipientUser() != null ? notification.getRecipientUser().getUsername() : null);
        dto.setTriggerUserId(notification.getTriggerUser() != null ? notification.getTriggerUser().getId() : null);
        dto.setTriggerUsername(notification.getTriggerUser() != null ? notification.getTriggerUser().getUsername() : null);
        dto.setTriggerUserImageId(notification.getTriggerUser() != null ? notification.getTriggerUser().getImageId() : null);
        dto.setReviewId(notification.getReview() != null ? notification.getReview().getId() : null);
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setIsRead(notification.isRead());
        dto.setMessage(notification.getMessage());

        return dto;
    }

    public List<NotificationDTO> toDTOList(List<Notification> notifications) {
        if (notifications == null) {
            return null;
        }

        return notifications.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Notification toEntity(NotificationDTO dto) {
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

    public List<Notification> toEntityList(List<NotificationDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}

