package ar.edu.itba.paw.api.mapper.resource;

import ar.edu.itba.paw.api.models.resources.NotificationResource;
import ar.edu.itba.paw.api.models.links.managers.NotificationLinkManager;
import ar.edu.itba.paw.api.dto.NotificationDTO;
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

