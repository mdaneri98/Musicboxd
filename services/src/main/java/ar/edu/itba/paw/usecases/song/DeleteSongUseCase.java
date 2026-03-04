package ar.edu.itba.paw.usecases.song;

import ar.edu.itba.paw.domain.song.SongId;
import ar.edu.itba.paw.domain.song.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteSongUseCase implements DeleteSong {

    private final SongRepository songRepository;

    @Autowired
    public DeleteSongUseCase(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    @Transactional
    public void execute(DeleteSongCommand command) {
        songRepository.deleteReviewsFromSong(command.id());
        songRepository.delete(new SongId(command.id()));
    }
}
