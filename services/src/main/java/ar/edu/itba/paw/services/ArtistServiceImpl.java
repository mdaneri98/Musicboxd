package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.dtos.ArtistDTO;
import ar.edu.itba.paw.persistence.ArtistDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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
    @Transactional(readOnly = true)
    public Optional<Artist> find(long id) {
        return artistDao.find(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artist> findAll() {
        return artistDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artist> findPaginated(FilterType filterType, int page, int pageSize) {
        return artistDao.findPaginated(filterType, pageSize, (page - 1) * pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artist> findByNameContaining(String sub) {
        return artistDao.findByNameContaining(sub);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artist> findBySongId(long id) {
        return artistDao.findBySongId(id);
    }

    @Override
    @Transactional
    public Artist create(Artist artist) {
        return artistDao.create(artist);
    }

    @Override
    @Transactional
    public Artist update(Artist artist) {
        return artistDao.update(artist);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        Optional<Artist> artist = artistDao.find(id);
        if (artist.isEmpty())
            return false;

        // Delete Images
        List<Album> list = albumService.findByArtistId(id);
        list.forEach(album -> imageService.delete(album.getImage().getId()));
        imageService.delete(artist.get().getImage().getId());

        return artistDao.delete(id);
    }

    @Override
    @Transactional
    public boolean delete(Artist artist) {
        if (artist.getId() == null || artist.getImage() == null || artist.getId() < 1 || artist.getImage().getId() < 1)
            return false;

        imageService.delete(artist.getImage().getId());
        return artistDao.delete(artist.getId());
    }

    @Override
    @Transactional
    public Artist create(ArtistDTO artistDTO) {
        Image image = imageService.create(artistDTO.getImage());
        Artist artist = new Artist(artistDTO.getName(), artistDTO.getBio(), image);

        artist.setCreatedAt(LocalDate.now());
        artist.setUpdatedAt(LocalDate.now());
        artist.setRatingCount(0);
        artist.setAvgRating(0f);
        artist = artistDao.create(artist);

        if( artistDTO.getAlbums() != null ) {
            albumService.createAll(artistDTO.getAlbums(), artist.getId());
        }
        return artist;
    }

    @Override
    @Transactional
    public Artist update(ArtistDTO artistDTO) {
        Artist artist = artistDao.find(artistDTO.getId()).get();
        artist.setName(artistDTO.getName());
        artist.setBio(artistDTO.getBio());
        artist.setImage(new Image(artist.getImage().getId(), artistDTO.getImage()));

        artist = artistDao.update(artist);

        if (artistDTO.getAlbums() != null) {
            albumService.updateAll(artistDTO.getAlbums(), artist.getId());
        }
        return artist;
    }

    @Override
    @Transactional
    public boolean updateRating(Long artistId, Float roundedAvgRating, Integer ratingAmount) {
        return artistDao.updateRating(artistId, roundedAvgRating, ratingAmount);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReviewed(long userId, long artistId) {
        return artistDao.hasUserReviewed(userId, artistId);
    }
}
