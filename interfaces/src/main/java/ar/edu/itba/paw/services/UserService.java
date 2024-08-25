package ar.edu.itba.paw.services;

import ar.edu.itba.paw.Album;
import ar.edu.itba.paw.User;

import java.util.List;
import java.util.Optional;

public interface UserService {


    Optional<User> findById(long id);
    List<User> findAll();

    int save(User user);

    int update(User user);

    int deleteById(long id);

}
