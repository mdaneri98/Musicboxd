package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.domain.user.UserRepository;
import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.usecases.album.GetAlbum;
import ar.edu.itba.paw.services.mappers.LegacyAlbumMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetUserFavoriteAlbumsUseCase implements GetUserFavoriteAlbums {

    private final UserRepository userRepository;
    private final GetAlbum getAlbum;
    private final LegacyAlbumMapper legacyAlbumMapper;

    @Autowired
    public GetUserFavoriteAlbumsUseCase(UserRepository userRepository, GetAlbum getAlbum, LegacyAlbumMapper legacyAlbumMapper) {
        this.userRepository = userRepository;
        this.getAlbum = getAlbum;
        this.legacyAlbumMapper = legacyAlbumMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Album> execute(Long userId, Integer page, Integer size) {
        List<Long> albumIds = userRepository.getFavoriteAlbumIds(new UserId(userId), page, size);
        return albumIds.stream()
            .map(getAlbum::execute)
            .map(legacyAlbumMapper::toLegacyModel)
            .collect(Collectors.toList());
    }
}
