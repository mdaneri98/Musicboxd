package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.user.User;
import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.domain.user.UserRepository;
import ar.edu.itba.paw.exception.not_found.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateUserReviewCountUseCase implements UpdateUserReviewCount {

    private final UserRepository userRepository;

    public UpdateUserReviewCountUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void decrement(Long userId) {
        User user = userRepository.findById(new UserId(userId))
            .orElseThrow(() -> new UserNotFoundException(userId));
        user.decrementReviewCount();
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void increment(Long userId) {
        User user = userRepository.findById(new UserId(userId))
            .orElseThrow(() -> new UserNotFoundException(userId));
        user.incrementReviewCount();
        userRepository.save(user);
    }
}
