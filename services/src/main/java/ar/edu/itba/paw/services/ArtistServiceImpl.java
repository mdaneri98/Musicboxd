package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.dtos.ArtistDTO;
import ar.edu.itba.paw.persistence.ArtistDao;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.logging.Filter;

@Service
public class ArtistServiceImpl implements ArtistService {
    private final ArtistDao artistDao;
    private final ImageService imageService;
    private final AlbumService albumService;

    public ArtistServiceImpl(ArtistDao artistDao, ImageService imageService, AlbumService albumService) {
        this.artistDao = artistDao;
        this.imageService = imageService;
        this.albumService = albumService;
    }

    @Override
    public Optional<Artist> findById(long id) {
        return artistDao.findById(id);
    }

    @Override
    public List<Artist> findAll() {
        return artistDao.findAll();
    }

    @Override
    public List<Artist> findPaginated(FilterType filterType, int page, int pageSize) {
        return artistDao.findPaginated(filterType, pageSize, (page - 1) * pageSize);
    }

    @Override
    public List<Artist> findByNameContaining(String sub) {
        return artistDao.findByNameContaining(sub);
    }

    @Override
    public List<Artist> findBySongId(long id) {
        return artistDao.findBySongId(id);
    }

    @Override
    public long save(Artist artist) {
        return artistDao.save(artist);
    }

    @Override
    public int update(Artist artist) {
        return artistDao.update(artist);
    }

    @Override
    public int deleteById(long id) {
        Optional<Artist> artist = artistDao.findById(id);
        if (artist.isEmpty()) {
            return 0;
        }

        imageService.delete(artist.get().getImgId());
        return artistDao.deleteById(id);
    }

    @Override
    public int delete(Artist artist) {
        imageService.delete(artist.getImgId());
        return artistDao.deleteById(artist.getId());
    }


    //********************************************************************** Testing
    @Override
    public Artist save(ArtistDTO artistDTO) {
        long imgId = imageService.save(artistDTO.getImage(), false);
        Artist artist = new Artist(artistDTO.getName(), artistDTO.getBio(),imgId);

        artist = artistDao.saveX(artist);

        if( artistDTO.getAlbums() != null ) {
            albumService.save(artistDTO.getAlbums(), artist);
        }
        return artist;
    }

    @Override
    public Artist update(ArtistDTO artistDTO) {
        long imgId = imageService.update(artistDTO.getImgId(), artistDTO.getImage());
        Artist artist = new Artist(artistDTO.getId(), artistDTO.getName(), artistDTO.getBio(), imgId);

        artist = artistDao.updateX(artist);

        if (artistDTO.getAlbums() != null) {
            albumService.update(artistDTO.getAlbums(), artist);
        }
        return artist;
    }
}

