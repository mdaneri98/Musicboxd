package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ArtistService extends CrudService<Artist> {

    List<Artist> findBySongId(long id);

    List<Artist> findByNameContaining(String sub);

    long save(Artist artist);

    long save(Artist artist, MultipartFile imageFile);

    int update(Artist artist);

    int update(Artist artist, Artist updatedArtist, MultipartFile imageFile);

    int delete(Artist artist);

}
