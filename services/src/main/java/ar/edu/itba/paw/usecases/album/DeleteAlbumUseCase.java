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
public class DeleteAlbumUseCase implements DeleteAlbum {

    private final AlbumRepository albumRepository;

    @Autowired
    public DeleteAlbumUseCase(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    @Override
    public void execute(DeleteAlbumCommand command) {
        AlbumId albumId = new AlbumId(command.albumId());

        Album album = albumRepository.findById(albumId)
            .orElseThrow(() -> new AlbumNotFoundException(command.albumId()));

        albumRepository.deleteReviewsFromAlbum(albumId);
        albumRepository.delete(albumId);
    }
}
