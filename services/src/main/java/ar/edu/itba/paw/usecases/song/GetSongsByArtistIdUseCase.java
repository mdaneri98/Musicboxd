package ar.edu.itba.paw.usecases.song;

import ar.edu.itba.paw.domain.song.Song;
import ar.edu.itba.paw.domain.song.SongRepository;
import ar.edu.itba.paw.models.FilterType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetSongsByArtistIdUseCase implements GetSongsByArtistId {

    private final SongRepository songRepository;

    @Autowired
    public GetSongsByArtistIdUseCase(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> execute(Long artistId, FilterType filterType, int page, int size) {
        return songRepository.findByArtistId(artistId, filterType, page, size);
    }
}
