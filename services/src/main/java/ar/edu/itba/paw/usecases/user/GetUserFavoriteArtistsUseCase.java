package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.domain.user.UserRepository;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.services.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetUserFavoriteArtistsUseCase implements GetUserFavoriteArtists {

    private final UserRepository userRepository;
    private final ArtistService artistService;

    @Autowired
    public GetUserFavoriteArtistsUseCase(UserRepository userRepository, ArtistService artistService) {
        this.userRepository = userRepository;
        this.artistService = artistService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artist> execute(Long userId, Integer page, Integer size) {
        List<Long> artistIds = userRepository.getFavoriteArtistIds(new UserId(userId), page, size);
        return artistIds.stream()
            .map(artistService::findById)
            .collect(Collectors.toList());
    }
}
