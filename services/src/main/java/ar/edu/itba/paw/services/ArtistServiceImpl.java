package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.persistence.ArtistDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.paw.exception.not_found.ArtistNotFoundException;

@Service
public class ArtistServiceImpl implements ArtistService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArtistServiceImpl.class);
    private final ArtistDao artistDao;
    private final ImageService imageService;
    private final AlbumService albumService;
    private final UserService userService;

    public ArtistServiceImpl(ArtistDao artistDao, ImageService imageService, AlbumService albumService, UserService userService) {
        this.artistDao = artistDao;
        this.imageService = imageService;
        this.albumService = albumService;
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public Artist findById(Long id) {
        return artistDao.findById(id).orElseThrow(() -> new ArtistNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artist> findAll() {
        return artistDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artist> findPaginated(FilterType filterType, Integer page, Integer pageSize) {
        return artistDao.findPaginated(filterType, pageSize, (page - 1) * pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artist> findByNameContaining(String sub, Integer page, Integer size) {
        return artistDao.findByNameContaining(sub, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artist> findBySongId(Long id) {
        return artistDao.findBySongId(id);
    }

    @Override
    @Transactional
    public Boolean updateRating(Long artistId) {
        LOGGER.info("Updating rating for artist ID: {}", artistId);
        List<Review> reviews = findReviewsByArtistId(artistId);
        Double avgRating = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        Double roundedAvgRating = Math.round(avgRating * 100.0) / 100.0;
        Integer ratingAmount = reviews.size();
        Boolean updated = artistDao.updateRating(artistId, roundedAvgRating, ratingAmount);
        
        if (updated) LOGGER.info("Artist rating updated. New average rating: {}, Total reviews: {}", roundedAvgRating, ratingAmount);
        else LOGGER.error("Artist rating not updated. New average rating: {}, Total reviews: {}", roundedAvgRating, ratingAmount);
        return updated;
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        LOGGER.info("Attempting to delete artist with ID: {}", id);
        Artist artist = artistDao.findById(id).orElseThrow(() -> new ArtistNotFoundException(id));

        albumService.findByArtistId(id).forEach(album -> albumService.delete(album.getId()));

        List<Long> userIds = new ArrayList<>();
        artistDao.findReviewsByArtistId(id).forEach(review -> userIds.add(review.getUser().getId()));
        artistDao.deleteReviewsFromArtist(id);
        userIds.forEach(userService::updateUserReviewAmount);
        Boolean deleted = artistDao.delete(id);
        imageService.delete(artist.getImage().getId());
        if (deleted) LOGGER.info("Artist with ID {} deleted successfully", id);
        else LOGGER.error("Failed to delete artist with ID {}", id);
        return deleted;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findReviewsByArtistId(Long artistId) {
        return new ArrayList<>(artistDao.findReviewsByArtistId(artistId));
    }

    @Override
    @Transactional
    public Artist create(Artist artistInput) {
        LOGGER.info("Creating new artist: {}", artistInput.getName());

        Image image;
        if (artistInput.getImage() == null || artistInput.getImage().getId() == null) {
            image = imageService.findById(imageService.getDefaultImgId());
        } else {
            image = imageService.findById(artistInput.getImage().getId());
        }

        Artist artist = new Artist(artistInput.getName(), artistInput.getBio(), image);
        artist = artistDao.create(artist);
        LOGGER.info("Artist created successfully with ID: {}", artist.getId());

        if (artistInput.getAlbums() != null && !artistInput.getAlbums().isEmpty()) {
            LOGGER.info("Creating albums for artist: {} (ID: {})", artist.getName(), artist.getId());
            albumService.createAll(artistInput.getAlbums(), artist);
        }
        return artist;
    }

    @Override
    @Transactional
    public Artist update(Artist artistInput) {
        LOGGER.info("Updating artist: {} (ID: {})", artistInput.getName(), artistInput.getId());

        Artist artist = artistDao.findById(artistInput.getId()).orElseThrow(() -> new ArtistNotFoundException(artistInput.getId()));
        
        if (artistInput.getImage() != null && artistInput.getImage().getId() != null) {
            artist.setImage(imageService.findById(artistInput.getImage().getId()));
        }
        
        if (artistInput.getName() != null) artist.setName(artistInput.getName());
        if (artistInput.getBio() != null) artist.setBio(artistInput.getBio());
        if (artistInput.getAlbums() != null && !artistInput.getAlbums().isEmpty()) albumService.updateAll(artistInput.getAlbums(), artist);
        artist = artistDao.update(artist);
        LOGGER.info("Artist updated successfully");
        return artist;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean hasUserReviewed(Long userId, Long artistId) {
        return artistDao.hasUserReviewed(userId, artistId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countAll() {
        return artistDao.countAll();
    }

    @Override
    public Artist findAndSetContextDependentFields(Long id, Long loggedUserId) {
        Artist artist = findById(id);
        setContextDependentFields(artist, loggedUserId);
        return artist;
    }

    @Override
    public void setContextDependentFields(Artist artist, Long loggedUserId) {
        if (loggedUserId == null) {
            artist.setIsReviewed(false);
            artist.setIsFavorite(false);
        } else {
            artist.setIsReviewed(hasUserReviewed(loggedUserId, artist.getId()));
            artist.setIsFavorite(userService.isArtistFavorite(loggedUserId, artist.getId()));
        }
    }
}
