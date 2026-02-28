package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.domain.user.UserRepository;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.usecases.song.GetSong;
import ar.edu.itba.paw.services.mappers.LegacySongMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetUserFavoriteSongsUseCase implements GetUserFavoriteSongs {

    private final UserRepository userRepository;
    private final GetSong getSong;
    private final LegacySongMapper legacySongMapper;

    @Autowired
    public GetUserFavoriteSongsUseCase(UserRepository userRepository, GetSong getSong, LegacySongMapper legacySongMapper) {
        this.userRepository = userRepository;
        this.getSong = getSong;
        this.legacySongMapper = legacySongMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> execute(Long userId, Integer page, Integer size) {
        List<Long> songIds = userRepository.getFavoriteSongIds(new UserId(userId), page, size);
        return songIds.stream()
            .map(getSong::execute)
            .map(legacySongMapper::toLegacyModel)
            .collect(Collectors.toList());
    }
}
