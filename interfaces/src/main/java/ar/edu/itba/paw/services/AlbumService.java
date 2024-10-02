package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface AlbumService {

    Optional<Album> findById(long id);

    List<Album> findByArtistId(long id);

    List<Album> findAll();

    List<Album> findByTitleContaining(String sub);

    long save(Album album);

    long save(Album album, MultipartFile imageFile);

    int update(Album album);

    int update(Album album, Album updatedAlbum, MultipartFile imageFile);

    int delete(Album album);

}
