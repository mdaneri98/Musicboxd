package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddFavoriteArtistUseCase implements AddFavoriteArtist {

    private final UserRepository userRepository;

    @Autowired
    public AddFavoriteArtistUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void execute(Long userId, Long artistId) {
        userRepository.addFavoriteArtist(new UserId(userId), artistId);
    }
}
