package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<User> findById(long id);
    List<User> findAll();

    int save(User user);

    int update(User user);

    int deleteById(long id);

    Optional<User> findByEmail(String email);
}
