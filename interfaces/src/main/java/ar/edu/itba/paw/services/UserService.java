package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {


    Optional<User> findById(long id);
    List<User> findAll();

    int create(User user);

    int update(User user);

    int deleteById(long id);

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String email);

    void createVerification(User user);
    boolean verify(String code);

}
