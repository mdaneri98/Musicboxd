package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.user.User;
import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetUserFollowersUseCase implements GetUserFollowers {

    private final UserRepository userRepository;

    @Autowired
    public GetUserFollowersUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> execute(Long userId, Integer page, Integer size) {
        List<UserId> followerIds = userRepository.getFollowerIds(new UserId(userId), page, size);
        return followerIds.stream()
            .map(followerId -> userRepository.findById(followerId).orElse(null))
            .filter(u -> u != null)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long count(Long userId) {
        return userRepository.countFollowers(new UserId(userId));
    }
}
