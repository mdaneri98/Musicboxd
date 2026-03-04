package ar.edu.itba.paw.usecases.album;

import ar.edu.itba.paw.domain.album.Album;
import ar.edu.itba.paw.domain.album.AlbumRepository;
import ar.edu.itba.paw.domain.artist.Artist;
import ar.edu.itba.paw.domain.artist.ArtistId;
import ar.edu.itba.paw.domain.artist.ArtistRepository;
import ar.edu.itba.paw.views.AlbumView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetAlbumsByArtistIdViewUseCase implements GetAlbumsByArtistIdView {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;

    @Autowired
    public GetAlbumsByArtistIdViewUseCase(AlbumRepository albumRepository, ArtistRepository artistRepository) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlbumView> execute(Long artistId) {
        List<Album> albums = albumRepository.findByArtistId(artistId);

        String artistName = null;
        if (artistId != null) {
            Artist artist = artistRepository.findById(new ArtistId(artistId)).orElse(null);
            if (artist != null) {
                artistName = artist.getName();
            }
        }

        final String finalArtistName = artistName;
        return albums.stream()
                .map(album -> new AlbumView(
                        album.getId().getValue(),
                        album.getTitle(),
                        album.getGenre(),
                        album.getReleaseDate(),
                        album.getImageId(),
                        album.getArtistId(),
                        finalArtistName,
                        album.getRatingCount(),
                        album.getAvgRating(),
                        album.getCreatedAt(),
                        album.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }
}
