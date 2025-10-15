package ar.edu.itba.paw.api.mapper;

import ar.edu.itba.paw.api.resources.NotificationResource;
import ar.edu.itba.paw.api.utils.linkManagers.NotificationLinkManager;
import ar.edu.itba.paw.models.dtos.NotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationResourceMapper implements ResourceMapper<NotificationDTO, NotificationResource> {

    @Autowired
    private NotificationLinkManager notificationLinkManager;

    @Override
    public NotificationResource toResource(NotificationDTO dto, String baseUrl) {
        NotificationResource resource = new NotificationResource(dto);
        notificationLinkManager.addNotificationLinks(resource, baseUrl);
        return resource;
    }

    @Override
    public List<NotificationResource> toResourceList(List<NotificationDTO> dtos, String baseUrl) {
        return dtos.stream()
                .map(dto -> toResource(dto, baseUrl))
                .collect(Collectors.toList());
    }
}

