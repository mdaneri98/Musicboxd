package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.user.User;
import java.util.List;

public interface GetAllUsers {
    List<User> execute(String search, Integer page, Integer size);
    Long count();
}
