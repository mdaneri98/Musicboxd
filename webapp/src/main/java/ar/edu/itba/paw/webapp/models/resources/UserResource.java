package ar.edu.itba.paw.webapp.models.resources;

import ar.edu.itba.paw.webapp.dto.UserDTO;

/**
 * HATEOAS resource wrapper for User entities
 */
public class UserResource extends Resource<UserDTO> {

    private final UserDTO item;

    public UserResource(UserDTO item) {
        this.item = item;
    }

    @Override
    public UserDTO getData() {
        return item;
    }
}
