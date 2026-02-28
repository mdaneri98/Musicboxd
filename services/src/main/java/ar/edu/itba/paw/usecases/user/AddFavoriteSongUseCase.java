package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddFavoriteSongUseCase implements AddFavoriteSong {

    private final UserRepository userRepository;

    @Autowired
    public AddFavoriteSongUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void execute(Long userId, Long songId) {
        userRepository.addFavoriteSong(new UserId(userId), songId);
    }
}
