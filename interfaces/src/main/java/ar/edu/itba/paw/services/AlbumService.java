package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.dtos.AlbumDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface AlbumService extends CrudService<Album> {

    List<Album> findByArtistId(long id);

    List<Album> findByTitleContaining(String sub);

    long save(Album album);

    int update(Album album);

    int delete(Album album);

    Album save(AlbumDTO albumDTO, Artist artist);
    boolean save(List<AlbumDTO> albumsDTO, Artist artist);
    Album update(AlbumDTO albumDTO, Artist artist);
    boolean update(List<AlbumDTO> albumsDTO, Artist artist);

}
