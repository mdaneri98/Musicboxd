package ar.edu.itba.paw.webapp.models.resources;

import ar.edu.itba.paw.webapp.dto.NotificationDTO;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class NotificationResource extends Resource<NotificationDTO> {

    @JsonUnwrapped
    private final NotificationDTO item;

    public NotificationResource(NotificationDTO item) {
        this.item = item;
    }
}

