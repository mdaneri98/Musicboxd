package ar.edu.itba.paw.usecases.song;

import ar.edu.itba.paw.domain.song.Song;
import ar.edu.itba.paw.domain.song.SongId;
import ar.edu.itba.paw.domain.song.SongRepository;
import ar.edu.itba.paw.exception.not_found.SongNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateSongUseCase implements UpdateSong {

    private final SongRepository songRepository;

    @Autowired
    public UpdateSongUseCase(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    @Transactional
    public Song execute(UpdateSongCommand command) {
        Song song = songRepository.findById(new SongId(command.id()))
            .orElseThrow(() -> new SongNotFoundException(command.id()));

        song.updateInfo(command.title(), command.duration(), command.trackNumber(), command.albumId());

        if (command.artistIds() != null) {
            song.setArtists(command.artistIds());
        }

        return songRepository.save(song);
    }
}
