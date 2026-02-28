package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.user.User;

public interface UpdateUserConfig {
    User execute(UpdateUserConfigCommand command);
}
