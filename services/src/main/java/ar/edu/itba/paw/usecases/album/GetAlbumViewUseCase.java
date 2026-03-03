package ar.edu.itba.paw.usecases.album;

import ar.edu.itba.paw.domain.album.Album;
import ar.edu.itba.paw.domain.album.AlbumId;
import ar.edu.itba.paw.domain.album.AlbumRepository;
import ar.edu.itba.paw.domain.artist.Artist;
import ar.edu.itba.paw.domain.artist.ArtistId;
import ar.edu.itba.paw.domain.artist.ArtistRepository;
import ar.edu.itba.paw.exception.not_found.AlbumNotFoundException;
import ar.edu.itba.paw.views.AlbumView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAlbumViewUseCase implements GetAlbumView {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;

    @Autowired
    public GetAlbumViewUseCase(AlbumRepository albumRepository, ArtistRepository artistRepository) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AlbumView execute(Long id) {
        Album album = albumRepository.findById(new AlbumId(id))
                .orElseThrow(() -> new AlbumNotFoundException(id));

        String artistName = null;
        if (album.getArtistId() != null) {
            Artist artist = artistRepository.findById(new ArtistId(album.getArtistId()))
                    .orElse(null);
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
    }
}
