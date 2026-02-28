package ar.edu.itba.paw.usecases.song;

import ar.edu.itba.paw.domain.song.Song;
import ar.edu.itba.paw.domain.song.SongId;
import ar.edu.itba.paw.domain.song.SongRepository;
import ar.edu.itba.paw.exception.not_found.SongNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetSongUseCase implements GetSong {

    private final SongRepository songRepository;

    @Autowired
    public GetSongUseCase(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Song execute(Long id) {
        return songRepository.findById(new SongId(id))
            .orElseThrow(() -> new SongNotFoundException(id));
    }
}
