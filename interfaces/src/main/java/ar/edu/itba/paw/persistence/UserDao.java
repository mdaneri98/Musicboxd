package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<User> findById(long id);

    List<User> findAll();

    int create(String username, String email, String password);

    int createFollowing(User loggedUser, User following);

    int undoFollowing(User loggedUser, User following);

    int update(Long user, String username, String email, String password, String name, String bio, LocalDateTime updated_at, boolean verified, boolean moderator, Long imgId, Integer followers_amount, Integer following_amount, Integer review_amount);

    int deleteById(long id);

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String email);

}
