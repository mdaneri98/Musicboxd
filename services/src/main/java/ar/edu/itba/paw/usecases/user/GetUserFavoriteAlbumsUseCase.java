package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.domain.user.UserRepository;
import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.services.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetUserFavoriteAlbumsUseCase implements GetUserFavoriteAlbums {

    private final UserRepository userRepository;
    private final AlbumService albumService;

    @Autowired
    public GetUserFavoriteAlbumsUseCase(UserRepository userRepository, AlbumService albumService) {
        this.userRepository = userRepository;
        this.albumService = albumService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Album> execute(Long userId, Integer page, Integer size) {
        List<Long> albumIds = userRepository.getFavoriteAlbumIds(new UserId(userId), page, size);
        return albumIds.stream()
            .map(albumService::findById)
            .collect(Collectors.toList());
    }
}
