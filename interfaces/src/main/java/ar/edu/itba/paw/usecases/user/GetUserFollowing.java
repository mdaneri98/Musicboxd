package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.user.User;
import java.util.List;

public interface GetUserFollowing {
    List<User> execute(Long userId, Integer page, Integer size);
    Long count(Long userId);
}
