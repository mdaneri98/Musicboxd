package ar.edu.itba.paw.services;

import ar.edu.itba.paw.domain.services.RatingService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.persistence.AlbumDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.paw.exception.not_found.AlbumNotFoundException;

@Service
public class AlbumServiceImpl implements AlbumService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlbumServiceImpl.class);

    private final AlbumDao albumDao;
    private final ImageService imageService;
    private final SongService songService;
    private final UserService userService;
    private final RatingService ratingService;

    public AlbumServiceImpl(AlbumDao albumDao, ImageService imageService, SongService songService, UserService userService,
                            RatingService ratingService) {
        this.albumDao = albumDao;
        this.imageService = imageService;
        this.songService = songService;
        this.userService = userService;
        this.ratingService = ratingService;
    }

    @Override
    @Transactional(readOnly = true)
    public Album findById(Long id) {
        return albumDao.findById(id).orElseThrow(() -> new AlbumNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Album> findPaginated(FilterType filterType, Integer page, Integer pageSize) {
        return albumDao.findPaginated(filterType, pageSize, (page - 1) * pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Album> findByArtistId(Long id) {
        return albumDao.findByArtistId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Album> findAll() {
        return albumDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Album> findByTitleContaining(String sub, Integer page, Integer size) {
        return albumDao.findByTitleContaining(sub, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findReviewsByAlbumId(Long albumId) {
        return new ArrayList<>(albumDao.findReviewsByAlbumId(albumId));
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        Album album = albumDao.findById(id).orElseThrow(() -> new AlbumNotFoundException(id));
        List<Long> userIds = new ArrayList<>();
        albumDao.findReviewsByAlbumId(id).forEach(review -> userIds.add(review.getUser().getId()));
        albumDao.deleteReviewsFromAlbum(id);
        userIds.forEach(userService::updateUserReviewAmount);
        album.getSongs().forEach(song -> songService.delete(song.getId()));
        Boolean deleted = albumDao.delete(album.getId());
        imageService.delete(album.getImage().getId());
        if (deleted) LOGGER.info("Album {} (ID: {}) deleted successfully", album.getTitle(), album.getId());
        else LOGGER.error("Failed to delete album {} (ID: {})", album.getTitle(), album.getId());
        return deleted;
    }

    @Override
    @Transactional
    public Album create(Album albumInput) {
        LOGGER.info("Creating new album: {} for artist ID: {}", albumInput.getTitle(), 
                albumInput.getArtist() != null ? albumInput.getArtist().getId() : null);
        
        Image image;
        if (albumInput.getImage() == null || albumInput.getImage().getId() == null) {
            image = imageService.findById(imageService.getDefaultImgId());
        } else {
            image = imageService.findById(albumInput.getImage().getId());
        }

        Album album = new Album(albumInput.getTitle(), image, albumInput.getGenre(), 
                albumInput.getArtist(), albumInput.getReleaseDate());
        album = albumDao.create(album);
        LOGGER.info("Album created successfully with ID: {}", album.getId());

        if (albumInput.getSongs() != null && !albumInput.getSongs().isEmpty()) {
            LOGGER.info("Creating songs for album: {} (ID: {})", album.getTitle(), album.getId());
            songService.createAll(albumInput.getSongs(), album);
        }
        return album;
    }

    @Override
    @Transactional
    public Boolean createAll(List<Album> albums, Artist artist) {
        LOGGER.info("Creating multiple albums for artist ID: {}", artist.getId());
        for (Album album : albums) {
            album.setArtist(artist);
            create(album);
        }
        LOGGER.info("All albums created successfully for artist ID: {}", artist.getId());
        return true;
    }

    @Override
    @Transactional
    public Album update(Album albumInput) {
        LOGGER.info("Updating album with ID: {}", albumInput.getId());
        Album album = albumDao.findById(albumInput.getId()).orElseThrow(() -> new AlbumNotFoundException(albumInput.getId()));
        
        if (albumInput.getImage() != null && albumInput.getImage().getId() != null) {
            album.setImage(imageService.findById(albumInput.getImage().getId()));
        }
        
        if (albumInput.getTitle() != null) album.setTitle(albumInput.getTitle());
        if (albumInput.getGenre() != null) album.setGenre(albumInput.getGenre());
        if (albumInput.getReleaseDate() != null) album.setReleaseDate(albumInput.getReleaseDate());
        if (albumInput.getSongs() != null && !albumInput.getSongs().isEmpty()) songService.updateAll(albumInput.getSongs(), albumInput);

        album = albumDao.update(album);
        LOGGER.info("Album updated successfully");

        return album;
    }

    @Override
    @Transactional
    public Boolean updateAll(List<Album> albums, Artist artist) {
        LOGGER.info("Updating multiple albums for artist ID: {}", artist.getId());
        for (Album albumInput : albums) {
            if (albumInput.getArtist() == null) {
                albumInput.setArtist(new Artist(artist.getId()));
            }
            
            if (albumInput.getId() != null && albumInput.getId() != 0) {
                update(albumInput);
            } else {
                create(albumInput);
            }
        }
        LOGGER.info("All albums updated successfully for artist ID: {}", artist.getId());
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean hasUserReviewed(Long userId, Long albumId) {
        return albumDao.hasUserReviewed(userId, albumId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countAll() {
        return albumDao.countAll();
    }

    @Override
    @Transactional
    public Boolean updateRating(Long albumId) {
        LOGGER.info("Updating rating for album ID: {}", albumId);

        List<Review> reviews = findReviewsByAlbumId(albumId);
        RatingService.RatingResult result = ratingService.calculate(reviews);
        Boolean updated = albumDao.updateRating(albumId, result.average(), result.count());

        if (updated) LOGGER.info("Album rating updated. New average rating: {}, Total reviews: {}", result.average(), result.count());
        else LOGGER.error("Album rating not updated. New average rating: {}, Total reviews: {}", result.average(), result.count());

        return updated;
    }

}
