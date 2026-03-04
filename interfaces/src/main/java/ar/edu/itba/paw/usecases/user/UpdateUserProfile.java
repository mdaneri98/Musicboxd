package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.user.User;

public interface UpdateUserProfile {
    User execute(UpdateUserProfileCommand command);
}
