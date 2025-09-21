package ar.edu.itba.paw.api.models;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.dtos.UserDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

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
