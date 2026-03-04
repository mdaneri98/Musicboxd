package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FollowUserUseCase implements FollowUser {

    private final UserRepository userRepository;

    @Autowired
    public FollowUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void execute(Long userId, Long targetUserId) {
        userRepository.addFollower(new UserId(targetUserId), new UserId(userId));
    }
}
