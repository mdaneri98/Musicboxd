package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.domain.user.UserRepository;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.usecases.artist.GetArtist;
import ar.edu.itba.paw.services.mappers.LegacyArtistMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetUserFavoriteArtistsUseCase implements GetUserFavoriteArtists {

    private final UserRepository userRepository;
    private final GetArtist getArtist;
    private final LegacyArtistMapper legacyArtistMapper;

    @Autowired
    public GetUserFavoriteArtistsUseCase(UserRepository userRepository, GetArtist getArtist, LegacyArtistMapper legacyArtistMapper) {
        this.userRepository = userRepository;
        this.getArtist = getArtist;
        this.legacyArtistMapper = legacyArtistMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artist> execute(Long userId, Integer page, Integer size) {
        List<Long> artistIds = userRepository.getFavoriteArtistIds(new UserId(userId), page, size);
        return artistIds.stream()
            .map(getArtist::execute)
            .map(legacyArtistMapper::toLegacyModel)
            .collect(Collectors.toList());
    }
}
