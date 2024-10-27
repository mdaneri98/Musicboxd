package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.dtos.ArtistDTO;
import ar.edu.itba.paw.persistence.ArtistDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ArtistServiceImpl implements ArtistService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArtistServiceImpl.class);
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
        LOGGER.info("Creating new artist: {}", artist.getName());
        Artist createdArtist = artistDao.create(artist);
        LOGGER.info("Artist created successfully with ID: {}", createdArtist.getId());
        return createdArtist;
    }

    @Override
    @Transactional
    public Artist update(Artist artist) {
        LOGGER.info("Updating artist with ID: {}", artist.getId());
        Artist updatedArtist = artistDao.update(artist);
        LOGGER.info("Artist updated successfully");
        return updatedArtist;
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        LOGGER.info("Attempting to delete artist with ID: {}", id);
        Optional<Artist> artist = artistDao.find(id);
        if (artist.isEmpty()) {
            LOGGER.warn("Artist with ID {} not found for deletion", id);
            return false;
        }

        // Delete Images
        List<Album> list = albumService.findByArtistId(id);
        list.forEach(album -> {
            LOGGER.info("Deleting image for album: {} (ID: {})", album.getTitle(), album.getId());
            imageService.delete(album.getImage().getId());
        });
        LOGGER.info("Deleting image for artist: {} (ID: {})", artist.get().getName(), id);
        imageService.delete(artist.get().getImage().getId());

        artistDao.deleteReviewsFromArtist(id);
        boolean deleted = artistDao.delete(id);
        if (deleted) {
            LOGGER.info("Artist with ID {} deleted successfully", id);
        } else {
            LOGGER.error("Failed to delete artist with ID {}", id);
        }
        return deleted;
    }

    @Override
    @Transactional
    public boolean delete(Artist artist) {
        if (artist.getId() == null || artist.getImage() == null || artist.getId() < 1 || artist.getImage().getId() < 1) {
            LOGGER.warn("Invalid artist data for deletion: {}", artist);
            return false;
        }
        LOGGER.info("Deleting image for artist: {} (ID: {})", artist.getName(), artist.getId());
        imageService.delete(artist.getImage().getId());
        boolean deleted = artistDao.delete(artist.getId());
        if (deleted) {
            LOGGER.info("Artist {} (ID: {}) deleted successfully", artist.getName(), artist.getId());
        } else {
            LOGGER.error("Failed to delete artist {} (ID: {})", artist.getName(), artist.getId());
        }
        return deleted;
    }

    @Override
    @Transactional
    public Artist create(ArtistDTO artistDTO) {
        LOGGER.info("Creating new artist from DTO: {}", artistDTO.getName());
        Image image = imageService.create(artistDTO.getImage());
        Artist artist = new Artist(artistDTO.getName(), artistDTO.getBio(), image);

        artist.setCreatedAt(LocalDate.now());
        artist.setUpdatedAt(LocalDate.now());
        artist.setRatingCount(0);
        artist.setAvgRating(0f);
        artist = artistDao.create(artist);
        LOGGER.info("Artist created successfully with ID: {}", artist.getId());

        if (artistDTO.getAlbums() != null) {
            LOGGER.info("Creating albums for artist: {} (ID: {})", artist.getName(), artist.getId());
            albumService.createAll(artistDTO.getAlbums(), artist.getId());
        }
        return artist;
    }

    @Override
    @Transactional
    public Artist update(ArtistDTO artistDTO) {
        LOGGER.info("Updating artist from DTO: {} (ID: {})", artistDTO.getName(), artistDTO.getId());

        Artist artist = artistDao.find(artistDTO.getId()).get();
        artist.setName(artistDTO.getName());
        artist.setBio(artistDTO.getBio());
        artist.setImage(new Image(artist.getImage().getId(), artistDTO.getImage()));

        artist = artistDao.update(artist);
        LOGGER.info("Artist updated successfully");

        if (artistDTO.getAlbums() != null) {
            LOGGER.info("Updating albums for artist: {} (ID: {})", artist.getName(), artist.getId());
            albumService.updateAll(artistDTO.getAlbums(), artist.getId());
        }
        return artist;
    }

    @Override
    @Transactional
    public boolean updateRating(Long artistId, Float roundedAvgRating, Integer ratingAmount) {
        LOGGER.info("Updating rating for artist with ID: {}", artistId);
        boolean updated = artistDao.updateRating(artistId, roundedAvgRating, ratingAmount);
        if (updated) {
            LOGGER.info("Rating updated successfully for artist ID: {}", artistId);
        } else {
            LOGGER.error("Failed to update rating for artist ID: {}", artistId);
        }
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReviewed(long userId, long artistId) {
        return artistDao.hasUserReviewed(userId, artistId);
    }
}
