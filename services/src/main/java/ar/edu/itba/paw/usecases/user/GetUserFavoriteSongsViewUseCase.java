package ar.edu.itba.paw.usecases.user;

import ar.edu.itba.paw.domain.album.Album;
import ar.edu.itba.paw.domain.album.AlbumRepository;
import ar.edu.itba.paw.domain.song.Song;
import ar.edu.itba.paw.views.SongView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GetUserFavoriteSongsViewUseCase implements GetUserFavoriteSongsView {

    private final GetUserFavoriteSongs getUserFavoriteSongs;
    private final AlbumRepository albumRepository;

    @Autowired
    public GetUserFavoriteSongsViewUseCase(GetUserFavoriteSongs getUserFavoriteSongs,
                                            AlbumRepository albumRepository) {
        this.getUserFavoriteSongs = getUserFavoriteSongs;
        this.albumRepository = albumRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SongView> execute(Long userId, Integer page, Integer size) {
        List<Song> songs = getUserFavoriteSongs.execute(userId, page, size);

        if (songs.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> albumIds = songs.stream()
            .map(Song::getAlbumId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        Map<Long, Album> albumMap = albumRepository.findByIds(albumIds);

        return songs.stream()
            .map(song -> {
                String albumTitle = null;
                Long albumImageId = null;
                if (song.getAlbumId() != null) {
                    Album album = albumMap.get(song.getAlbumId());
                    if (album != null) {
                        albumTitle = album.getTitle();
                        albumImageId = album.getImageId();
                    }
                }
                return new SongView(
                    song.getId().getValue(),
                    song.getTitle(),
                    song.getDuration(),
                    song.getTrackNumber(),
                    song.getAlbumId(),
                    albumTitle,
                    albumImageId,
                    song.getArtistIds(),
                    song.getRatingCount(),
                    song.getAvgRating(),
                    song.getCreatedAt(),
                    song.getUpdatedAt()
                );
            })
            .collect(Collectors.toList());
    }
}
