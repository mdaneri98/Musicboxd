package ar.edu.itba.paw.usecases.song;

import ar.edu.itba.paw.domain.album.Album;
import ar.edu.itba.paw.domain.album.AlbumId;
import ar.edu.itba.paw.domain.album.AlbumRepository;
import ar.edu.itba.paw.domain.song.Song;
import ar.edu.itba.paw.domain.song.SongId;
import ar.edu.itba.paw.domain.song.SongRepository;
import ar.edu.itba.paw.exception.not_found.SongNotFoundException;
import ar.edu.itba.paw.views.SongView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetSongViewUseCase implements GetSongView {

    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;

    @Autowired
    public GetSongViewUseCase(SongRepository songRepository, AlbumRepository albumRepository) {
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public SongView execute(Long id) {
        Song song = songRepository.findById(new SongId(id))
                .orElseThrow(() -> new SongNotFoundException(id));

        String albumTitle = null;
        Long albumImageId = null;
        if (song.getAlbumId() != null) {
            Album album = albumRepository.findById(new AlbumId(song.getAlbumId()))
                    .orElse(null);
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
    }
}
