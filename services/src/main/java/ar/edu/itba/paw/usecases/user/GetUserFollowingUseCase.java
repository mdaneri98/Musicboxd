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
public class GetUserFollowingUseCase implements GetUserFollowing {

    private final UserRepository userRepository;

    @Autowired
    public GetUserFollowingUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> execute(Long userId, Integer page, Integer size) {
        List<UserId> followingIds = userRepository.getFollowingIds(new UserId(userId), page, size);
        return followingIds.stream()
            .map(followingId -> userRepository.findById(followingId).orElse(null))
            .filter(u -> u != null)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long count(Long userId) {
        return userRepository.countFollowing(new UserId(userId));
    }
}
