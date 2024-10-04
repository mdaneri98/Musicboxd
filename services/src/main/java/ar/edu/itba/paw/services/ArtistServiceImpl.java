package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.persistence.ArtistDao;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class ArtistServiceImpl implements ArtistService {
    private final ArtistDao artistDao;
    private final ImageService imageService;

    public ArtistServiceImpl(ArtistDao artistDao, ImageService imageService) {
        this.artistDao = artistDao;
        this.imageService = imageService;
    }

    @Override
    public Optional<Artist> find(long id) {
        return artistDao.find(id);
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
    public Artist create(Artist artist) {
        return artistDao.create(artist);
    }

    public Artist save(Artist artist, MultipartFile imageFile) {
        artist.setImgId(imageService.save(imageFile, false));
        return artistDao.create(artist);
    }

    @Override
    public Artist update(Artist artist) {
        return artistDao.update(artist);
    }

    public Artist update(Artist artist, Artist updatedArtist, MultipartFile imageFile) {
        long imgId = imageService.update(artist.getImgId(), imageFile);
        updatedArtist.setImgId(imgId);
        if(updatedArtist.getId() == null) {updatedArtist.setId(imgId);}
        if(!artist.equals(updatedArtist)) {
            artist.setName(updatedArtist.getName());
            artist.setBio(updatedArtist.getBio());
            artist.setImgId(imgId);
        }
        return artistDao.update(artist);
    }

    @Override
    public boolean delete(long id) {
        return artistDao.delete(id);
    }

}

