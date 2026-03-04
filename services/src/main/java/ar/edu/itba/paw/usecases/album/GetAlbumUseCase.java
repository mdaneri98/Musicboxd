package ar.edu.itba.paw.usecases.album;

import ar.edu.itba.paw.domain.album.Album;
import ar.edu.itba.paw.domain.album.AlbumId;
import ar.edu.itba.paw.domain.album.AlbumRepository;
import ar.edu.itba.paw.exception.not_found.AlbumNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetAlbumUseCase implements GetAlbum {

    private final AlbumRepository albumRepository;

    @Autowired
    public GetAlbumUseCase(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    @Override
    public Album execute(Long albumId) {
        return albumRepository.findById(new AlbumId(albumId))
            .orElseThrow(() -> new AlbumNotFoundException(albumId));
    }
}
