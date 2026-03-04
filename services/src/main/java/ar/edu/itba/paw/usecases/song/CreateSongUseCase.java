package ar.edu.itba.paw.usecases.song;

import ar.edu.itba.paw.domain.song.Song;
import ar.edu.itba.paw.domain.song.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateSongUseCase implements CreateSong {

    private final SongRepository songRepository;

    @Autowired
    public CreateSongUseCase(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    @Transactional
    public Song execute(CreateSongCommand command) {
        Song song = Song.create(
            command.title(),
            command.duration(),
            command.trackNumber(),
            command.albumId(),
            command.artistIds()
        );

        Song savedSong = songRepository.save(song);

        if (command.artistIds() != null && !command.artistIds().isEmpty()) {
            for (Long artistId : command.artistIds()) {
                songRepository.saveSongArtist(savedSong.getId().getValue(), artistId);
            }
        }

        return savedSong;
    }
}
