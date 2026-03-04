package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.domain.user.UserRepository;
import ar.edu.itba.paw.domain.artist.Artist;
import ar.edu.itba.paw.usecases.artist.GetArtist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetUserFavoriteArtistsUseCase implements GetUserFavoriteArtists {

    private final UserRepository userRepository;
    private final GetArtist getArtist;

    @Autowired
    public GetUserFavoriteArtistsUseCase(UserRepository userRepository, GetArtist getArtist) {
        this.userRepository = userRepository;
        this.getArtist = getArtist;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artist> execute(Long userId, Integer page, Integer size) {
        List<Long> artistIds = userRepository.getFavoriteArtistIds(new UserId(userId), page, size);
        return artistIds.stream()
            .map(getArtist::execute)
            .collect(Collectors.toList());
    }
}
