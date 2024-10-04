package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.dtos.ArtistDTO;
import ar.edu.itba.paw.persistence.ArtistDao;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Artist update(Artist artist) {
        return artistDao.update(artist);
    }

    @Override
    public boolean delete(long id) {
        Optional<Artist> artist = artistDao.find(id);
        if (artist.isEmpty())
            return false;

        imageService.delete(artist.get().getImgId());
        return artistDao.delete(id);
    }

    @Override
    public boolean delete(Artist artist) {
        if (artist.getId() == null || artist.getImgId() == null || artist.getId() < 1 || artist.getImgId() < 1)
            return false;

        imageService.delete(artist.getImgId());
        return artistDao.delete(artist.getId());
    }

    @Override
    public Artist create(ArtistDTO artistDTO) {
        long imgId = imageService.save(artistDTO.getImage(), false);
        Artist artist = new Artist(artistDTO.getName(), artistDTO.getBio(),imgId);

        artist = artistDao.create(artist);

        if( artistDTO.getAlbums() != null ) {
            albumService.createAll(artistDTO.getAlbums(), artist.getId());
        }
        return artist;
    }

    @Override
    public Artist update(ArtistDTO artistDTO) {
        long imgId = imageService.update(artistDTO.getImgId(), artistDTO.getImage());

        Artist artist = artistDao.find(artistDTO.getId()).get();
        artist.setName(artistDTO.getName());
        artist.setBio(artistDTO.getBio());
        artist.setImgId(imgId);

        artist = artistDao.update(artist);

        if (artistDTO.getAlbums() != null) {
            albumService.updateAll(artistDTO.getAlbums(), artist.getId());
        }
        return artist;
    }
}

