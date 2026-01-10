package ar.edu.itba.paw.webapp.models.resources;

import ar.edu.itba.paw.webapp.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * HATEOAS resource wrapper for User entities
 */
public class UserResource extends Resource<UserDTO> {

    @JsonUnwrapped
    private final UserDTO item;

    public UserResource(UserDTO item) {
        this.item = item;
    }
}
