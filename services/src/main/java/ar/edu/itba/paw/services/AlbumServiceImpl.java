package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.utils.TimeUtils;
import ar.edu.itba.paw.models.dtos.AlbumDTO;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.persistence.AlbumDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AlbumServiceImpl implements AlbumService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlbumServiceImpl.class);

    private final AlbumDao albumDao;
    private final ImageService imageService;
    private final SongService songService;
    private final UserService userService;

    public AlbumServiceImpl(AlbumDao albumDao, ImageService imageService, SongService songService, UserService userService) {
        this.albumDao = albumDao;
        this.imageService = imageService;
        this.songService = songService;
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Album> find(long id) {
        Optional<Album> album = albumDao.find(id);
        album.ifPresent(a -> a.setFormattedReleaseDate(TimeUtils.formatDate(a.getReleaseDate())));
        return album;
    }

    @Transactional(readOnly = true)
    public List<Album> findPaginated(FilterType filterType, int page, int pageSize) {
        List<Album> albums = albumDao.findPaginated(filterType, pageSize, (page - 1) * pageSize);
        albums.forEach(a -> a.setFormattedReleaseDate(TimeUtils.formatDate(a.getReleaseDate())));
        return albums;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Album> findByArtistId(long id) {
        List<Album> albums = albumDao.findByArtistId(id);
        albums.forEach(a -> a.setFormattedReleaseDate(TimeUtils.formatDate(a.getReleaseDate())));
        return albums;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Album> findAll() {
        List<Album> albums = albumDao.findAll();
        albums.forEach(a -> a.setFormattedReleaseDate(TimeUtils.formatDate(a.getReleaseDate())));
        return albums;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Album> findByTitleContaining(String sub) {
        List<Album> albums = albumDao.findByTitleContaining(sub);
        albums.forEach(a -> a.setFormattedReleaseDate(TimeUtils.formatDate(a.getReleaseDate())));
        return albums;
    }

    @Override
    @Transactional
    public Album create(Album album) {
        LOGGER.info("Creating new album: {}", album.getTitle());

        Optional<Image> optionalImage = imageService.findById(imageService.getDefaultProfileImgId());
        if (optionalImage.isEmpty())
            throw new IllegalArgumentException("La imagen profile-default no existe.");

        Album createdAlbum = albumDao.create(album);
        LOGGER.info("Album created successfully with ID: {}", createdAlbum.getId());
        return createdAlbum;
    }

    @Override
    @Transactional
    public Album update(Album album) {
        LOGGER.info("Updating album with ID: {}", album.getId());
        Album updatedAlbum = albumDao.update(album);
        LOGGER.info("Album updated successfully");
        return updatedAlbum;
    }

    @Transactional
    public boolean delete(long id) {
        LOGGER.info("Attempting to delete album with ID: {}", id);
        Optional<Album> album = albumDao.find(id);
        if (album.isEmpty()) {
            LOGGER.warn("Album with ID {} not found for deletion", id);
            return false;
        }
        List<Long> userIds = new ArrayList<>();
        albumDao.findReviewsByAlbumId(id).forEach(review -> userIds.add(review.getUser().getId()));
        albumDao.deleteReviewsFromAlbum(id);
        userIds.forEach(userId -> userService.updateUserReviewAmount(userId));
        boolean deleted = albumDao.delete(id);
        if (deleted) {
            LOGGER.info("Album with ID {} deleted successfully", id);
        } else {
            LOGGER.error("Failed to delete album with ID {}", id);
        }
        return deleted;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlbumReview> findReviewsByAlbumId(long albumId) {
        return albumDao.findReviewsByAlbumId(albumId);
    }

    @Override
    @Transactional
    public boolean delete(Album album) {
        if (album.getId() == null) {
            LOGGER.warn("Invalid album data for deletion: {}", album);
            return false;
        }
        LOGGER.info("Deleting album: {} (ID: {})", album.getTitle(), album.getId());
        List<Long> userIds = new ArrayList<>();
        albumDao.findReviewsByAlbumId(album.getId()).forEach(review -> userIds.add(review.getUser().getId()));
        albumDao.deleteReviewsFromAlbum(album.getId());
        userIds.forEach(userId -> userService.updateUserReviewAmount(userId));
        album.getSongs().forEach(song -> songService.delete(song.getId()));
        boolean deleted = albumDao.delete(album.getId());
        if (deleted) {
            LOGGER.info("Album {} (ID: {}) deleted successfully", album.getTitle(), album.getId());
        } else {
            LOGGER.error("Failed to delete album {} (ID: {})", album.getTitle(), album.getId());
        }
        return deleted;
    }

    @Override
    @Transactional
    public Album create(AlbumDTO albumDTO, long artistId) {
        LOGGER.info("Creating new album from DTO: {} for artist ID: {}", albumDTO.getTitle(), artistId);
        Image image = imageService.create(albumDTO.getImage());
        Album album = new Album(albumDTO.getTitle(), image, albumDTO.getGenre(), new Artist(artistId), albumDTO.getReleaseDate());
        album.setCreatedAt(LocalDateTime.now());
        album.setUpdatedAt(LocalDateTime.now());
        album.setRatingCount(0);
        album.setAvgRating(0d);

        album = albumDao.create(album);
        LOGGER.info("Album created successfully with ID: {}", album.getId());

        if (albumDTO.getSongs() != null) {
            LOGGER.info("Creating songs for album: {} (ID: {})", album.getTitle(), album.getId());
            songService.createAll(albumDTO.getSongs(), album);
        }
        return album;
    }

    @Override
    @Transactional
    public boolean createAll(List<AlbumDTO> albumsDTO, long artistId) {
        LOGGER.info("Creating multiple albums for artist ID: {}", artistId);
        for (AlbumDTO albumDTO : albumsDTO) {
            if (!albumDTO.isDeleted()) {
                create(albumDTO, artistId);
            }
        }
        LOGGER.info("All albums created successfully for artist ID: {}", artistId);
        return true;
    }

    @Override
    @Transactional
    public Album update(AlbumDTO albumDTO) {
        LOGGER.info("Updating album with ID: {}", albumDTO.getId());
        Optional<Image> optionalImage = imageService.update(new Image(albumDTO.getImgId(), albumDTO.getImage()));

        Album album = albumDao.find(albumDTO.getId()).get();
        album.setTitle(albumDTO.getTitle());
        album.setImage(optionalImage.get());
        album.setGenre(albumDTO.getGenre());
        album.setReleaseDate(albumDTO.getReleaseDate());

        album = albumDao.update(album);
        LOGGER.info("Album updated successfully");

        if (albumDTO.getSongs() != null) {
            LOGGER.info("Updating songs for album: {} (ID: {})", album.getTitle(), album.getId());
            songService.updateAll(albumDTO.getSongs(), album);
        }
        return album;
    }

    @Override
    @Transactional
    public boolean updateAll(List<AlbumDTO> albumsDTO, long artistId) {
        LOGGER.info("Updating multiple albums for artist ID: {}", artistId);
        for (AlbumDTO albumDTO : albumsDTO) {
            if (albumDTO.getId() != 0) {
                if (albumDTO.isDeleted()) {
                    delete(new Album(albumDTO.getId(), albumDTO.getTitle(), imageService.findById(albumDTO.getImgId()).get(), albumDTO.getGenre(), new Artist(artistId), albumDTO.getReleaseDate()));
                } else {
                    update(albumDTO);
                }
            } else {
                if (!albumDTO.isDeleted()) {
                    create(albumDTO, artistId);
                }
            }
        }
        LOGGER.info("All albums updated successfully for artist ID: {}", artistId);
        return true;
    }

    @Override
    @Transactional
    public boolean updateRating(long albumId, Double newRating, int newRatingAmount) {
        LOGGER.info("Updating rating for album ID: {} to {} with {} ratings", albumId, newRating, newRatingAmount);
        boolean updated = albumDao.updateRating(albumId, newRating, newRatingAmount);
        if (updated) {
            LOGGER.info("Rating updated successfully for album ID: {}", albumId);
        } else {
            LOGGER.error("Failed to update rating for album ID: {}", albumId);
        }
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReviewed(long userId, long albumId) {
        return albumDao.hasUserReviewed(userId, albumId);
    }

}
