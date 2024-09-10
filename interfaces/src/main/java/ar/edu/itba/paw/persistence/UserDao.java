package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<User> findById(long id);

    List<User> findAll();

    int create(User user);

    int createFollowing(User loggedUser, User following);

    int undoFollowing(User loggedUser, User following);

    int update(User user);

    int deleteById(long id);

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String email);

}
