package ar.edu.itba.paw.usecases.song;

import ar.edu.itba.paw.domain.song.Song;
import ar.edu.itba.paw.domain.song.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetSongsByAlbumIdUseCase implements GetSongsByAlbumId {

    private final SongRepository songRepository;

    @Autowired
    public GetSongsByAlbumIdUseCase(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> execute(Long albumId) {
        return songRepository.findByAlbumId(albumId);
    }
}
