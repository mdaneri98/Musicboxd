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
import ar.edu.itba.paw.services.exception.AlbumNotFoundException;
import ar.edu.itba.paw.services.mappers.AlbumMapper;
import java.util.Optional;

@Service
public class AlbumServiceImpl implements AlbumService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlbumServiceImpl.class);

    private final AlbumDao albumDao;
    private final ImageService imageService;
    private final SongService songService;
    private final UserService userService;
    private final AlbumMapper albumMapper;

    public AlbumServiceImpl(AlbumDao albumDao, ImageService imageService, SongService songService, UserService userService, AlbumMapper albumMapper) {
        this.albumDao = albumDao;
        this.imageService = imageService;
        this.songService = songService;
        this.userService = userService;
        this.albumMapper = albumMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public AlbumDTO findById(Long id) {
        Album album = albumDao.findById(id).orElseThrow(() -> new AlbumNotFoundException("Album with id " + id + " not found"));
        album.setFormattedReleaseDate(TimeUtils.formatDate(album.getReleaseDate()));
        return albumMapper.toDTO(album);
    }

    @Transactional(readOnly = true)
    public List<AlbumDTO> findPaginated(FilterType filterType, int page, int pageSize) {
        List<Album> albums = albumDao.findPaginated(filterType, pageSize, (page - 1) * pageSize);
        albums.forEach(a -> a.setFormattedReleaseDate(TimeUtils.formatDate(a.getReleaseDate())));
        return albumMapper.toDTOList(albums);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlbumDTO> findByArtistId(long id) {
        List<Album> albums = albumDao.findByArtistId(id);
        albums.forEach(a -> a.setFormattedReleaseDate(TimeUtils.formatDate(a.getReleaseDate())));
        return albumMapper.toDTOList(albums);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlbumDTO> findAll() {
        List<Album> albums = albumDao.findAll();
        albums.forEach(a -> a.setFormattedReleaseDate(TimeUtils.formatDate(a.getReleaseDate())));
        return albumMapper.toDTOList(albums);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlbumDTO> findByTitleContaining(String sub) {
        List<Album> albums = albumDao.findByTitleContaining(sub);
        albums.forEach(a -> a.setFormattedReleaseDate(TimeUtils.formatDate(a.getReleaseDate())));
        return albumMapper.toDTOList(albums);
    }

    @Transactional
    public boolean delete(long id) {
        LOGGER.info("Attempting to delete album with ID: {}", id);
        Optional<Album> album = albumDao.findById(id);
        if (album.isEmpty()) {
            LOGGER.warn("Album with ID {} not found for deletion", id);
            return false;
        }
        List<Long> userIds = new ArrayList<>();
        albumDao.findReviewsByAlbumId(id).forEach(review -> userIds.add(review.getUser().getId()));
        albumDao.deleteReviewsFromAlbum(id);
        userIds.forEach(userId -> userService.updateUserReviewAmount(userId));
        boolean deleted = albumDao.delete(id);
        imageService.delete(album.get().getImage().getId());
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
    public Boolean delete(Long id) {
        Album album = albumDao.findById(id).orElseThrow(() -> new AlbumNotFoundException("Album with id " + id + " not found"));
        List<Long> userIds = new ArrayList<>();
        albumDao.findReviewsByAlbumId(id).forEach(review -> userIds.add(review.getUser().getId()));
        albumDao.deleteReviewsFromAlbum(id);
        userIds.forEach(userId -> userService.updateUserReviewAmount(userId));
        album.getSongs().forEach(song -> songService.delete(song.getId()));
        boolean deleted = albumDao.delete(album.getId());
        imageService.delete(album.getImage().getId());
        if (deleted) {
            LOGGER.info("Album {} (ID: {}) deleted successfully", album.getTitle(), album.getId());
        } else {
            LOGGER.error("Failed to delete album {} (ID: {})", album.getTitle(), album.getId());
        }
        return deleted;
    }

    @Override
    @Transactional
    public AlbumDTO create(AlbumDTO albumDTO) {
        LOGGER.info("Creating new album from DTO: {} for artist ID: {}", albumDTO.getTitle(), albumDTO.getArtistId());
        
        Image image;
        if (albumDTO.getImageId() == 0 && albumDTO.getImage().getImage().getBytes().length == 0)
            image = imageService.findById(imageService.getDefaultImgId()).get();
        else
            image = imageService.create(albumDTO.getImage().getImage().getBytes());

        Album album = new Album(albumDTO.getTitle(), image, albumDTO.getGenre(), new Artist(albumDTO.getArtistId()), albumDTO.getReleaseDate());
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
        return albumMapper.toDTO(album);
    }

    @Override
    @Transactional
    public boolean createAll(List<AlbumDTO> albumsDTO, long artistId) {
        LOGGER.info("Creating multiple albums for artist ID: {}", artistId);
        for (AlbumDTO albumDTO : albumsDTO) {
            if (!albumDTO.isDeleted()) {
                create(albumDTO);
            }
        }
        LOGGER.info("All albums created successfully for artist ID: {}", artistId);
        return true;
    }

    @Override
    @Transactional
    public AlbumDTO update(AlbumDTO albumDTO) {
        LOGGER.info("Updating album with ID: {}", albumDTO.getId());
        Optional<Image> optionalImage = imageService.update(new Image(albumDTO.getImage().getId(), albumDTO.getImage().getImage().getBytes()));

        Album album = albumDao.findById(albumDTO.getId()).get();
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
        return albumMapper.toDTO(album);
    }

    @Override
    @Transactional
    public boolean updateAll(List<AlbumDTO> albumsDTO, long artistId) {
        LOGGER.info("Updating multiple albums for artist ID: {}", artistId);
        for (AlbumDTO albumDTO : albumsDTO) {
            if (albumDTO.getId() != 0) {
                if (albumDTO.isDeleted()) {
                    delete(albumDTO.getId());
                } else {
                    update(albumDTO);
                }
            } else {
                if (!albumDTO.isDeleted()) {
                    create(albumDTO);
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
