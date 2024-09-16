package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserService {


    Optional<User> findById(long id);
    List<User> findAll();

    int create(String username, String email, String password);

    int createFollowing(User loggedUser, long followingUserId);

    int undoFollowing(User loggedUser, long followingUserId);

    int update(Long user, String username, String email, String password, String name, String bio, LocalDateTime updated_at, boolean verified, boolean moderator, Long imgId, Integer followers_amount, Integer following_amount, Integer review_amount);

    int deleteById(long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String email);

    int incrementReviewAmount(User user);
    int decrementReviewAmount(User user);
    void createVerification(User user);
    boolean verify(String code);

}
