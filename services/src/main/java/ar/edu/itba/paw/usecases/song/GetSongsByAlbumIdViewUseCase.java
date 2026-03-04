package ar.edu.itba.paw.usecases.song;

import ar.edu.itba.paw.domain.album.Album;
import ar.edu.itba.paw.domain.album.AlbumId;
import ar.edu.itba.paw.domain.album.AlbumRepository;
import ar.edu.itba.paw.domain.song.Song;
import ar.edu.itba.paw.domain.song.SongRepository;
import ar.edu.itba.paw.views.SongView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetSongsByAlbumIdViewUseCase implements GetSongsByAlbumIdView {

    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;

    @Autowired
    public GetSongsByAlbumIdViewUseCase(SongRepository songRepository, AlbumRepository albumRepository) {
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SongView> execute(Long albumId) {
        List<Song> songs = songRepository.findByAlbumId(albumId);

        Album album = albumId != null ? albumRepository.findById(new AlbumId(albumId)).orElse(null) : null;
        String albumTitle = album != null ? album.getTitle() : null;
        Long albumImageId = album != null ? album.getImageId() : null;

        return songs.stream()
                .map(song -> new SongView(
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
                ))
                .collect(Collectors.toList());
    }
}
