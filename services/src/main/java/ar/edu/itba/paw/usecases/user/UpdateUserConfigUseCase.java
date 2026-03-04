package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.user.User;
import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.domain.user.UserRepository;
import ar.edu.itba.paw.exception.not_found.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateUserConfigUseCase implements UpdateUserConfig {

    private final UserRepository userRepository;

    @Autowired
    public UpdateUserConfigUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User execute(UpdateUserConfigCommand command) {
        User user = userRepository.findById(new UserId(command.userId()))
            .orElseThrow(() -> new UserNotFoundException(command.userId()));

        if (command.preferredLanguage() != null) {
            user.setPreferredLanguage(command.preferredLanguage());
        }
        if (command.theme() != null) {
            user.setTheme(command.theme());
        }
        if (command.followNotificationsEnabled() != null) {
            user.setFollowNotificationsEnabled(command.followNotificationsEnabled());
        }
        if (command.likeNotificationsEnabled() != null) {
            user.setLikeNotificationsEnabled(command.likeNotificationsEnabled());
        }
        if (command.commentNotificationsEnabled() != null) {
            user.setCommentNotificationsEnabled(command.commentNotificationsEnabled());
        }
        if (command.reviewNotificationsEnabled() != null) {
            user.setReviewNotificationsEnabled(command.reviewNotificationsEnabled());
        }

        return userRepository.save(user);
    }
}
