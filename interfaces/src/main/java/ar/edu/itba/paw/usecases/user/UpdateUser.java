package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.user.User;

public interface UpdateUser {
    User execute(UpdateUserCommand command);
}
