package ar.edu.itba.paw.usecases.album;

import ar.edu.itba.paw.domain.album.Album;
import ar.edu.itba.paw.domain.album.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateAlbumUseCase implements CreateAlbum {

    private final AlbumRepository albumRepository;

    @Autowired
    public CreateAlbumUseCase(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    @Override
    public Album execute(CreateAlbumCommand command) {
        Album album = Album.create(
            command.title(),
            command.genre(),
            command.releaseDate(),
            command.imageId(),
            command.artistId()
        );
        return albumRepository.save(album);
    }
}
