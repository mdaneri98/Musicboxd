package ar.edu.itba.paw.usecases.song;

import ar.edu.itba.paw.domain.song.Song;
import ar.edu.itba.paw.domain.song.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetAllSongsUseCase implements GetAllSongs {

    private final SongRepository songRepository;

    @Autowired
    public GetAllSongsUseCase(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> execute(int page, int size) {
        return songRepository.findAll(page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public Long count() {
        return songRepository.countAll();
    }
}
