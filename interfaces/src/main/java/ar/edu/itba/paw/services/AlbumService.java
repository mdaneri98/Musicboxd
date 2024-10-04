package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface AlbumService extends CrudService<Album> {

    List<Album> findByArtistId(long id);

    List<Album> findByTitleContaining(String sub);

    Album save(Album album, MultipartFile imageFile);

    Album update(Album album, Album updatedAlbum, MultipartFile imageFile);

}
