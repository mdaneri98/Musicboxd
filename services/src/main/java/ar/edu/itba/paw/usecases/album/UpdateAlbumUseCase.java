package ar.edu.itba.paw.usecases.album;

import ar.edu.itba.paw.domain.album.Album;
import ar.edu.itba.paw.domain.album.AlbumId;
import ar.edu.itba.paw.domain.album.AlbumRepository;
import ar.edu.itba.paw.exception.not_found.AlbumNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateAlbumUseCase implements UpdateAlbum {

    private final AlbumRepository albumRepository;

    @Autowired
    public UpdateAlbumUseCase(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    @Override
    public Album execute(UpdateAlbumCommand command) {
        AlbumId albumId = new AlbumId(command.albumId());

        Album album = albumRepository.findById(albumId)
            .orElseThrow(() -> new AlbumNotFoundException(command.albumId()));

        if (command.title() != null || command.genre() != null || command.releaseDate() != null) {
            String newTitle = command.title() != null ? command.title() : album.getTitle();
            String newGenre = command.genre() != null ? command.genre() : album.getGenre();
            album.updateInfo(newTitle, newGenre, command.releaseDate());
        }

        if (command.imageId() != null) {
            album.updateImage(command.imageId());
        }

        if (command.artistId() != null) {
            album.updateArtist(command.artistId());
        }

        return albumRepository.save(album);
    }
}
