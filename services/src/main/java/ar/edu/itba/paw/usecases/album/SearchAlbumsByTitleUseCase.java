package ar.edu.itba.paw.usecases.album;

import ar.edu.itba.paw.domain.album.Album;
import ar.edu.itba.paw.domain.album.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SearchAlbumsByTitleUseCase implements SearchAlbumsByTitle {

    private final AlbumRepository albumRepository;

    @Autowired
    public SearchAlbumsByTitleUseCase(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    @Override
    public List<Album> execute(String titleSubstring, Integer page, Integer size) {
        return albumRepository.findByTitleContaining(titleSubstring, page, size);
    }
}
