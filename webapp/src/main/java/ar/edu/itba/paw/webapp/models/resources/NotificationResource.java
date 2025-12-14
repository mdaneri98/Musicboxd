package ar.edu.itba.paw.webapp.models.resources;

import ar.edu.itba.paw.webapp.dto.NotificationDTO;

public class NotificationResource extends Resource<NotificationDTO> {

    private final NotificationDTO item;

    public NotificationResource(NotificationDTO item) {
        this.item = item;
    }

    @Override
    public NotificationDTO getData() {
        return item;
    }
}

