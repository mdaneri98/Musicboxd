package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.utils.TimeUtils;
import ar.edu.itba.paw.models.dtos.AlbumDTO;
import ar.edu.itba.paw.persistence.AlbumDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import ar.edu.itba.paw.exception.not_found.AlbumNotFoundException;
import ar.edu.itba.paw.services.mappers.AlbumMapper;
import java.util.stream.Collectors;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
import ar.edu.itba.paw.services.mappers.ReviewMapper;
import ar.edu.itba.paw.services.utils.MergeUtils;

@Service
public class AlbumServiceImpl implements AlbumService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlbumServiceImpl.class);

    private final AlbumDao albumDao;
    private final ImageService imageService;
    private final SongService songService;
    private final UserService userService;
    private final AlbumMapper albumMapper;
    private final ReviewMapper reviewMapper;

    public AlbumServiceImpl(AlbumDao albumDao, ImageService imageService, SongService songService, UserService userService, AlbumMapper albumMapper, ReviewMapper reviewMapper) {
        this.albumDao = albumDao;
        this.imageService = imageService;
        this.songService = songService;
        this.userService = userService;
        this.albumMapper = albumMapper;
        this.reviewMapper = reviewMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public AlbumDTO findById(Long id, Long loggedUserId) {
        Album album = albumDao.findById(id).orElseThrow(() -> new AlbumNotFoundException(id));
        album.setFormattedReleaseDate(TimeUtils.formatDate(album.getReleaseDate()));
        AlbumDTO albumDTO = albumMapper.toDTO(album);
        if (loggedUserId != null) albumDTO.setReviewedByLoggedUser(hasUserReviewed(loggedUserId, album.getId()));
        return albumDTO;
    }

    @Transactional(readOnly = true)
    public List<AlbumDTO> findPaginated(FilterType filterType, Integer page, Integer pageSize) {
        List<Album> albums = albumDao.findPaginated(filterType, pageSize, (page - 1) * pageSize);
        albums.forEach(a -> a.setFormattedReleaseDate(TimeUtils.formatDate(a.getReleaseDate())));
        return albumMapper.toDTOList(albums);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlbumDTO> findByArtistId(Long id) {
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
    public List<AlbumDTO> findByTitleContaining(String sub, Integer page, Integer size) {
        List<Album> albums = albumDao.findByTitleContaining(sub, page, size);
        albums.forEach(a -> a.setFormattedReleaseDate(TimeUtils.formatDate(a.getReleaseDate())));
        return albumMapper.toDTOList(albums);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> findReviewsByAlbumId(Long albumId) {
        return albumDao.findReviewsByAlbumId(albumId).stream().map(reviewMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        Album album = albumDao.findById(id).orElseThrow(() -> new AlbumNotFoundException(id));
        List<Long> userIds = new ArrayList<>();
        albumDao.findReviewsByAlbumId(id).forEach(review -> userIds.add(review.getUser().getId()));
        albumDao.deleteReviewsFromAlbum(id);
        userIds.forEach(userId -> userService.updateUserReviewAmount(userId));
        album.getSongs().forEach(song -> songService.delete(song.getId()));
        Boolean deleted = albumDao.delete(album.getId());
        imageService.delete(album.getImage().getId());
        if (deleted) LOGGER.info("Album {} (ID: {}) deleted successfully", album.getTitle(), album.getId());
        else LOGGER.error("Failed to delete album {} (ID: {})", album.getTitle(), album.getId());
        return deleted;
    }

    @Override
    @Transactional
    public AlbumDTO create(AlbumDTO albumDTO) {
        LOGGER.info("Creating new album from DTO: {} for artist ID: {}", albumDTO.getTitle(), albumDTO.getArtistId());
        
        Image image;
        if (albumDTO.getImageId() == null) image = imageService.findById(imageService.getDefaultImgId());
        else image = imageService.findById(albumDTO.getImageId());

        Album album = new Album(albumDTO.getTitle(), image, albumDTO.getGenre(), new Artist(albumDTO.getArtistId()), albumDTO.getReleaseDate());
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
    public Boolean createAll(List<AlbumDTO> albumsDTO, Artist artist) {
        LOGGER.info("Creating multiple albums for artist ID: {}", artist.getId());
        for (AlbumDTO albumDTO : albumsDTO) {
            albumDTO.setArtistId(artist.getId());
            if (!albumDTO.isDeleted()) create(albumDTO);
        }
        LOGGER.info("All albums created successfully for artist ID: {}", artist.getId());
        return true;
    }

    @Override
    @Transactional
    public AlbumDTO update(AlbumDTO albumDTO) {
        LOGGER.info("Updating album with ID: {}", albumDTO.getId());
        Album album = albumDao.findById(albumDTO.getId()).orElseThrow(() -> new AlbumNotFoundException(albumDTO.getId()));
        if (albumDTO.getImageId() != null) album.setImage(imageService.findById(albumDTO.getImageId()));
        MergeUtils.mergeAlbumFields(album, albumDTO);
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
    public Boolean updateAll(List<AlbumDTO> albumsDTO, Artist artist) {
        LOGGER.info("Updating multiple albums for artist ID: {}", artist.getId());
        for (AlbumDTO albumDTO : albumsDTO) {
            albumDTO.setArtistId(artist.getId());
            if (albumDTO.getId() != 0 && albumDTO.getId() != null) {
                if (albumDTO.isDeleted()) delete(albumDTO.getId());
                else update(albumDTO);
            } else if (!albumDTO.isDeleted()) create(albumDTO);
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

        List<ReviewDTO> reviews = findReviewsByAlbumId(albumId);
        Double avgRating = reviews.stream().mapToInt(ReviewDTO::getRating).average().orElse(0.0);
        Double roundedAvgRating = Math.round(avgRating * 100.0) / 100.0;
        int ratingAmount = reviews.size();
        Boolean updated = albumDao.updateRating(albumId, roundedAvgRating, ratingAmount);

        if (updated) LOGGER.info("Album rating updated. New average rating: {}, Total reviews: {}", roundedAvgRating, ratingAmount);
        else LOGGER.error("Album rating not updated. New average rating: {}, Total reviews: {}", roundedAvgRating, ratingAmount);
        
        return updated;
    }

}
