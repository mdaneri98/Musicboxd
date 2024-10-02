package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Image;
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
    public Optional<Artist> findById(long id) {
        return artistDao.findById(id);
    }

    @Override
    public List<Artist> findAll() {
        return artistDao.findAll();
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

    public long save(Artist artist, MultipartFile imageFile) {
        artist.setImgId(imageService.save(imageFile, false));
        return artistDao.save(artist);
    }

    @Override
    public int update(Artist artist) {
        return artistDao.update(artist);
    }

    public int update(Artist artist, Artist updatedArtist, MultipartFile imageFile) {
        long imgId = imageService.update(artist.getImgId(), imageFile);
        updatedArtist.setImgId(imgId);
        if(updatedArtist.getId() == null) {updatedArtist.setId(imgId);}
        if(!artist.equals(updatedArtist)) {
            artist.setName(updatedArtist.getName());
            artist.setBio(updatedArtist.getBio());
            artist.setImgId(imgId);
            return artistDao.update(artist);
        }
        return 0;
    }

    @Override
    public int delete(Artist artist) {
        imageService.delete(artist.getImgId());
        return artistDao.deleteById(artist.getId());
    }
}

