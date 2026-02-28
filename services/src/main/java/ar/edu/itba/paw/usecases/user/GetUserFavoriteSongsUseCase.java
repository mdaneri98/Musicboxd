package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.domain.user.UserRepository;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.services.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetUserFavoriteSongsUseCase implements GetUserFavoriteSongs {

    private final UserRepository userRepository;
    private final SongService songService;

    @Autowired
    public GetUserFavoriteSongsUseCase(UserRepository userRepository, SongService songService) {
        this.userRepository = userRepository;
        this.songService = songService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> execute(Long userId, Integer page, Integer size) {
        List<Long> songIds = userRepository.getFavoriteSongIds(new UserId(userId), page, size);
        return songIds.stream()
            .map(songService::findById)
            .collect(Collectors.toList());
    }
}
