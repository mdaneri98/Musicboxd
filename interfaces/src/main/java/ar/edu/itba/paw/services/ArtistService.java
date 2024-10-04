package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.dtos.ArtistDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ArtistService extends CrudService<Artist> {

    List<Artist> findBySongId(long id);

    List<Artist> findByNameContaining(String sub);

    long save(Artist artist);

    int update(Artist artist);

    int delete(Artist artist);

    Artist save(ArtistDTO artistDTO);
    Artist update(ArtistDTO artistDTO);

}
