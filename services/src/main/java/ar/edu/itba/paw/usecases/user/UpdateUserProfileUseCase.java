package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.user.User;
import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.domain.user.UserRepository;
import ar.edu.itba.paw.exception.not_found.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateUserProfileUseCase implements UpdateUserProfile {

    private final UserRepository userRepository;

    @Autowired
    public UpdateUserProfileUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User execute(UpdateUserProfileCommand command) {
        User user = userRepository.findById(new UserId(command.userId()))
            .orElseThrow(() -> new UserNotFoundException(command.userId()));

        if (command.name() != null) {
            user.setName(command.name());
        }
        if (command.bio() != null) {
            user.setBio(command.bio());
        }
        if (command.imageId() != null) {
            user.setImageId(command.imageId());
        }

        return userRepository.save(user);
    }
}
