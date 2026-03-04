package ar.edu.itba.paw.usecases.album;

import ar.edu.itba.paw.domain.album.Album;
import ar.edu.itba.paw.domain.album.AlbumRepository;
import ar.edu.itba.paw.domain.artist.Artist;
import ar.edu.itba.paw.domain.artist.ArtistRepository;
import ar.edu.itba.paw.views.AlbumView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SearchAlbumsByTitleViewUseCase implements SearchAlbumsByTitleView {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;

    @Autowired
    public SearchAlbumsByTitleViewUseCase(AlbumRepository albumRepository, ArtistRepository artistRepository) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlbumView> execute(String title, Integer page, Integer size) {
        List<Album> albums = albumRepository.findByTitleContaining(title, page, size);

        Set<Long> artistIds = albums.stream()
            .map(Album::getArtistId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        Map<Long, Artist> artistMap = artistRepository.findByIds(artistIds);

        return albums.stream()
                .map(album -> {
                    String artistName = null;
                    if (album.getArtistId() != null) {
                        Artist artist = artistMap.get(album.getArtistId());
                        if (artist != null) {
                            artistName = artist.getName();
                        }
                    }
                    return new AlbumView(
                            album.getId().getValue(),
                            album.getTitle(),
                            album.getGenre(),
                            album.getReleaseDate(),
                            album.getImageId(),
                            album.getArtistId(),
                            artistName,
                            album.getRatingCount(),
                            album.getAvgRating(),
                            album.getCreatedAt(),
                            album.getUpdatedAt()
                    );
                })
                .collect(Collectors.toList());
    }
}
